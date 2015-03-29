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
import com.github.nyrkovalex.seed.Seed;
import com.github.nyrkovalex.seed.io.Io;
import java.util.logging.Logger;

final class RanFile implements Jsons.RWFile<Jsons.Ran> {

    private static final Logger LOG = Seed.logger(RanFile.class);
    static final String FILENAME = "ran.json";
    private final Json.File<JsonsRan> jsonFile;

    RanFile(Io.Fs fs, Json.Parser json) {
        super();
        this.jsonFile = json.file(fs.file(FILENAME), JsonsRan.class);
    }

    @Override
    public Jsons.Ran read() throws Io.Err {
        LOG.fine(() -> "reading migrations file " + FILENAME);
        return jsonFile.readIfExists().orElse(JsonsRan.empty());
    }

    @Override
    public void write(Jsons.Ran stuff) throws Io.Err, Json.Err {
        LOG.fine(() -> "writing to migrations file " + FILENAME);
        jsonFile.write((JsonsRan) stuff);
    }
}
