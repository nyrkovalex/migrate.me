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
import java.time.Instant;
import java.util.logging.Logger;

public class Database {

    private final static Logger LOG = Seed.logger(Database.class);
    private final static Io.Fs FS = Io.fs();
	private final static Sys.Clock CLOCK = Sys.clock();

	public static Executor executor(Db.Runner runner) {
		return new DbExecutor(FS, runner, CLOCK);
	}

    public static interface Executed {
        Instant on();
        String fileName();
    }

    public static interface Executor {
        Executed execute(String fileName);
    }

	public static class Err extends Exception {
		Err(Throwable cause) {
			super(cause);
		}
	}
}
