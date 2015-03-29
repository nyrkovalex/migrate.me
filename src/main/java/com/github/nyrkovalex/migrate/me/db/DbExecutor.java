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
package com.github.nyrkovalex.migrate.me.db;

import com.github.nyrkovalex.seed.db.Db;
import com.github.nyrkovalex.seed.io.Io;
import com.github.nyrkovalex.seed.Seed;
import com.github.nyrkovalex.seed.sys.Sys;

import java.util.logging.Logger;

class DbExecutor implements Database.Executor {

	private final static Logger LOG = Seed.logger(DbExecutor.class);

	private final Db.Runner runner;

	private final Io.Fs fs;
	private final Sys.Clock clock;

	DbExecutor(Io.Fs fs, Db.Runner conn, Sys.Clock clock) {
		this.fs = fs;
		this.runner = conn;
		this.clock = clock;
	}

	@Override
	public Database.Executed execute(String fileName) {
		try {
			String sql = fs.file(fileName).string();
			runner.run(sql);
			return new DbExecuted(fileName, clock.now());
		} catch (Db.Err | Io.Err err) {
			throw new RuntimeException(err);
		}
	}
}