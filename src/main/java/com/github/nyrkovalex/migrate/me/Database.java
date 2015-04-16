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

import com.github.nyrkovalex.seed.Db;
import com.github.nyrkovalex.seed.Io;
import com.github.nyrkovalex.seed.Sys;
import java.time.Instant;

public class Database {

	private final static Io.Fs FS = Io.fs();
	private final static Sys.Clock CLOCK = Sys.clock();

	public static Executor executor(Db.Runner runner) {
		return new DbExecutor(FS, runner, CLOCK);
	}

	public interface Executed {
		Instant on();
		String fileName();
	}

	public interface Executor {
		Executed execute(String fileName) throws Err;
	}

	public static class Err extends RuntimeException {
		Err(Throwable cause) {
			super(cause);
		}
	}
}

class DbExecuted implements Database.Executed {

	private final Instant on;
	private final String fileName;

	DbExecuted(String fileName, Instant on) {
		this.on = on;
		this.fileName = fileName;
	}

	@Override
	public Instant on() {
		return on;
	}

	@Override
	public String fileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return String.format("Executed %s on %s", fileName(), on());
	}
}

class DbExecutor implements Database.Executor {

	private final Db.Runner runner;
	private final Io.Fs fs;
	private final Sys.Clock clock;

	DbExecutor(Io.Fs fs, Db.Runner conn, Sys.Clock clock) {
		this.fs = fs;
		this.runner = conn;
		this.clock = clock;
	}

	@Override
	public Database.Executed execute(String fileName) throws Database.Err {
		try {
			String sql = fs.file(fileName).string();
			runner.run(sql);
			return new DbExecuted(fileName, clock.now());
		} catch (Db.Err | Io.Err err) {
			throw new Database.Err(err);
		}
	}
}
