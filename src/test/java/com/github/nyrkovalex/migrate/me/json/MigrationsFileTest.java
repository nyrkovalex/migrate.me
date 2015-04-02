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
package com.github.nyrkovalex.migrate.me.json;

import com.github.nyrkovalex.seed.Expect;
import com.github.nyrkovalex.seed.Io;
import com.github.nyrkovalex.seed.Json;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;

public class MigrationsFileTest extends Expect.Test {

    @Mock Io.Fs fs;
    @Mock Io.File file;
    @Mock Json.Parser parser;
    @Mock Json.File<JsonsMigrations> jsonFile;
    @Mock JsonsMigrations result;

    MigrationsFile mf;

    @Before
    public void setUp() {
        given(fs.file(MigrationsFile.FILENAME)).returns(file);
        given(parser.file(file, JsonsMigrations.class)).returns(jsonFile);

        mf = new MigrationsFile(fs, parser);
    }

    @Test
    public void testShouldReadMigrationsFile() throws Exception {
        given(jsonFile.read()).returns(result);
        expect(mf.read()).toBe(result);
    }
}
