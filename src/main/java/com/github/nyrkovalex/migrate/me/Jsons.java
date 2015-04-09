/*
 * The MIT License
 *
 * Copyright 2015 Alexander Nyrkov.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.nyrkovalex.migrate.me;

import java.time.Instant;
import java.util.List;

import com.github.nyrkovalex.seed.Io;
import com.github.nyrkovalex.seed.Json;
import com.github.nyrkovalex.seed.Seed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public final class Jsons {

	private final static Json.Parser PARSER = Json.parser();
	private final static Io.Fs FS = Io.fs();

	public static RWFile<Ran> ranFile() throws Io.Err {
		return new RanFile(FS, PARSER);
	}

	public static ROFile<Migrations> migrationsFile() throws Io.Err {
		return new MigrationsFile(FS, PARSER);
	}

	public static Ran.Item ranItem(String fileName, Instant on) {
		return new JsonsRan.Item(fileName, on.toString());
	}

	private Jsons() {
	}

	public static interface ROFile<T> {
		T read() throws Err;
	}

	public static interface RWFile<T> extends ROFile<T> {
		void write(T data) throws Err;
	}

	public static interface Driver {
		String className();
		String jar();
	}

	public static interface Migrations {
		Driver driver();
		String connectionString();
		List<String> once();
		List<String> repeat();
	}

	public static interface Ran {
		boolean canRun(String fileName);
		Ran addAll(List<Item> items);

		public static interface Item {
			Instant on();
			String file();
		}
	}

	public static class Err extends Exception {
		Err(Throwable cause) {
			super(cause);
		}

		static <T> T rethrow(Seed.UnsafeCall<T> call) throws Err {
			try {
				return call.call();
			} catch (Exception ex) {
				throw new Err(ex);
			}
		}

		static void rethrow(Seed.VoidUnsafeCall call) throws Err {
			try {
				call.call();
			} catch (Exception ex) {
				throw new Err(ex);
			}
		}
	}
}

class JsonsDriver implements Jsons.Driver {

	private final String className;
	private final String jar;

	private JsonsDriver(String className, String jar) {
		this.className = className;
		this.jar = jar;
	}

	@Override
	public String className() {
		return className;
	}

	@Override
	public String jar() {
		return jar;
	}

}

class JsonsMigrations implements Jsons.Migrations {

	private final JsonsDriver driver;
	private final String connectionString;
	private final List<String> once;
	private final List<String> repeat;

	JsonsMigrations(JsonsDriver driver, String connectionString, List<String> once, List<String> repeat) {
		this.driver = driver;
		this.connectionString = connectionString;
		this.once = Objects.isNull(once) ? Collections.emptyList() : Collections.unmodifiableList(once);
		this.repeat = Objects.isNull(repeat) ? Collections.emptyList() : Collections.unmodifiableList(repeat);
	}

	@Override
	public int hashCode() {
		return Objects.hash(once, repeat, driver, connectionString);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JsonsMigrations other = (JsonsMigrations) obj;
		return Objects.equals(this.once, other.once)
				&& Objects.equals(this.repeat, other.repeat)
				&& Objects.equals(this.driver, other.driver)
				&& Objects.equals(this.connectionString, other.connectionString);
	}

	@Override
	public Jsons.Driver driver() {
		return driver;
	}

	@Override
	public String connectionString() {
		return connectionString;
	}

	@Override
	public List<String> once() {
		return Optional.ofNullable(once).orElse(Collections.emptyList());
	}

	@Override
	public List<String> repeat() {
		return Optional.ofNullable(repeat).orElse(Collections.emptyList());
	}
}

class JsonsRan implements Jsons.Ran {

	public static Item run(String file) {
		return new Item(file, Instant.now().toString());
	}

	public static JsonsRan empty() {
		return new JsonsRan(Collections.emptyList());
	}

	private final List<JsonsRan.Item> ran;

	JsonsRan(List<JsonsRan.Item> items) {
		this.ran = Collections.unmodifiableList(items);
	}

	List<Item> items() {
		return ran;
	}

	@Override
	public boolean canRun(String file) {
		return !ran.stream().anyMatch(r -> r.file().equals(file));
	}

	@Override
	public Jsons.Ran addAll(List<Jsons.Ran.Item> executed) {
		ArrayList<JsonsRan.Item> ranCopy = new ArrayList<>(ran);
		executed.forEach(e -> ranCopy.add(Item.fromOther(e)));
		return new JsonsRan(ranCopy);
	}

	public static class Item implements Jsons.Ran.Item {

		static Item fromOther(Jsons.Ran.Item other) {
			if (other instanceof Item) {
				return (Item) other;
			} else {
				return new Item(other.file(), other.on().toString());
			}
		}

		private final String file;
		private final String on;

		Item(String file, String on) {
			this.file = file;
			this.on = on;
		}

		@Override
		public int hashCode() {
			return Objects.hash(file, on);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Item other = (Item) obj;
			return Objects.equals(this.file, other.file)
					&& Objects.equals(this.on, other.on);
		}

		@Override
		public Instant on() {
			return Instant.parse(on);
		}

		@Override
		public String file() {
			return file;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(ran);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JsonsRan other = (JsonsRan) obj;
		return Objects.equals(this.ran, other.ran);
	}
}

class MigrationsFile implements Jsons.ROFile<Jsons.Migrations> {

	private static final Logger LOG = Seed.logger(MigrationsFile.class);
	static final String FILENAME = "migrate.me.json";
	private final Json.File<JsonsMigrations> jsonFile;

	MigrationsFile(Io.Fs fs, Json.Parser json) throws Io.Err {
		this.jsonFile = json.file(fs.file(FILENAME), JsonsMigrations.class);
	}

	@Override
	public Jsons.Migrations read() throws Jsons.Err {
		LOG.fine(() -> "reading migrations file " + FILENAME);
		return Jsons.Err.rethrow(() -> jsonFile.read());
	}

}

class RanFile implements Jsons.RWFile<Jsons.Ran> {

	private static final Logger LOG = Seed.logger(RanFile.class);
	static final String FILENAME = "ran.json";
	private final Json.File<JsonsRan> jsonFile;

	RanFile(Io.Fs fs, Json.Parser json) throws Io.Err {
		super();
		this.jsonFile = json.file(fs.file(FILENAME), JsonsRan.class);
	}

	@Override
	public Jsons.Ran read() throws Jsons.Err {
		LOG.fine(() -> "reading migrations file " + FILENAME);
		return Jsons.Err.rethrow(() -> jsonFile.readIfExists().orElse(JsonsRan.empty()));
	}

	@Override
	public void write(Jsons.Ran stuff) throws Jsons.Err {
		LOG.fine(() -> "writing to migrations file " + FILENAME);
		Jsons.Err.rethrow(() -> jsonFile.write((JsonsRan) stuff));
	}
}
