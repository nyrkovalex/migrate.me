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

import com.github.nyrkovalex.migrate.me.json.Jsons;
import com.github.nyrkovalex.migrate.me.db.Database;
import com.github.nyrkovalex.seed.Db;
import com.github.nyrkovalex.seed.Plugins;
import com.github.nyrkovalex.seed.Seed;
import com.github.nyrkovalex.seed.Io;
import com.github.nyrkovalex.seed.Json;
import java.sql.Driver;
import java.util.List;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;

public class MigrateMe {

	private static final Logger LOG = Seed.logger(MigrateMe.class);

	public static void main(String[] args) throws Exception {
		Seed.Logging.init(true, MigrateMe.class);
		MigrateMe app = new MigrateMe(
				Jsons.migrationsFile(),
				Jsons.ranFile(),
				Plugins.loader()
		);
		app.run();
	}

	private final Jsons.ROFile<Jsons.Migrations> migrationsFile;
	private final Jsons.RWFile<Jsons.Ran> ranFile;
	private final Plugins.Loader plug;

	private MigrateMe(
			Jsons.ROFile<Jsons.Migrations> migrationsFile,
			Jsons.RWFile<Jsons.Ran> ranFile,
			Plugins.Loader plug
	) {
		this.migrationsFile = migrationsFile;
		this.ranFile = ranFile;
		this.plug = plug;
	}

	public void run() throws Exception {
		Jsons.Ran alreadyRan = ranFile.read();
		Jsons.Migrations migrations = migrationsFile.read();
		Jsons.Driver driverJson = migrations.driver();
		Plugins.Repo pluginLoader = plug.repo(driverJson.jar());
		Driver driver = (Driver) pluginLoader.instanceOf(driverJson.className());
		Db.Connection conn = Db.connectTo(migrations.connectionString()).with(driver);
		conn.transaction(t -> {
			Database.Executor executor = Database.executor(t);
			List<Jsons.Ran.Item> oneTimersRan = migrations.once().stream()
					.filter(alreadyRan::canRun)
					.map(executor::execute)
					.map(e -> Jsons.ranItem(e.fileName(), e.on()))
					.collect(toList());
			Jsons.Ran nowRan = alreadyRan.addAll(oneTimersRan);
			migrations.repeat().forEach(executor::execute);
			try {
				ranFile.write(nowRan);
			} catch (Io.Err | Json.Err err) {
				LOG.severe(() -> "Failed to log ran migrations " + err);
				return false;
			}
			return true;
		});
	}

}
