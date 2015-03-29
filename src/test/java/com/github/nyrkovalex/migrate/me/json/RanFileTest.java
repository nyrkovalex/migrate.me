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

import com.github.nyrkovalex.seed.json.Json;
import com.github.nyrkovalex.seed.Expect;
import com.github.nyrkovalex.seed.io.Io;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RanFileTest extends Expect.Test {

    @Mock Io.Fs fs;
    @Mock Io.File file;

    @Mock Json.Parser json;
    @Mock Json.File<JsonsRan> jsonFile;

    @Mock JsonsRan ran;

    RanFile ranFile;

    @Before
    public void setUp() throws Exception {
        given(fs.file(RanFile.FILENAME)).returns(file);
        given(json.file(file, JsonsRan.class)).returns(jsonFile);

        ranFile = new RanFile(fs, json);
    }

    @Test
    public void testShouldReturnEmptyRanSetWhenNoFilePresent() throws Exception {
        given(jsonFile.readIfExists()).returns(Optional.empty());
        Jsons.Ran read = ranFile.read();
        expect(read).toBe(JsonsRan.empty());
    }

    @Test
    public void testShouldReadFileWhenPresent() throws Exception {
        given(jsonFile.readIfExists()).returns(Optional.of(ran));
        Jsons.Ran read = ranFile.read();
        expect(read == ran).toBe(Boolean.TRUE);
    }

    @Test
    public void testShouldWriteRanFile() throws Exception {
        ranFile.write(ran);
        expect(jsonFile).toHaveCall().write(ran);
    }

}