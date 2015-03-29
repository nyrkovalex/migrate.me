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

import org.junit.Test;
import com.github.nyrkovalex.seed.Expect;
import com.github.nyrkovalex.seed.db.Db;
import com.github.nyrkovalex.seed.io.Io;
import com.github.nyrkovalex.seed.sys.Sys;
import java.time.Instant;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class DbExecutorTest extends Expect.Test {

	@Mock Io.Fs fs;
	@Mock Io.File file;
	@Mock Db.Connection conn;
	@Mock Sys.Clock clock;

	@InjectMocks DbExecutor executor;

	@Before
	public void setUp() throws Exception {
		given(fs.file("test")).returns(file);
		given(file.string()).returns("sql");
	}

	@Test
	public void testShouldExecuteFile() throws Exception {
		executor.execute("test");
		expect(conn).toHaveCall().run("sql");
	}

	@Test
	public void testShouldCreateExecutedWithCurretnTime() throws Exception {
		Instant expected = Instant.now();
		given(clock.now()).returns(expected);
		Database.Executed executed = executor.execute("test");
		expect(executed.on()).toBe(expected);
	}

	@Test
	public void testShouldCreateExecutedWithGivenName() throws Exception {
		Database.Executed executed = executor.execute("test");
		expect(executed.fileName()).toBe("test");
	}

}