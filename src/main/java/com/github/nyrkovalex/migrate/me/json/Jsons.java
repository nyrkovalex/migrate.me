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

import java.time.Instant;
import java.util.List;

import com.github.nyrkovalex.seed.Io;
import com.github.nyrkovalex.seed.Json;

public final class Jsons {

    private final static Json.Parser PARSER = Json.parser();
    private final static Io.Fs FS = Io.fs();

    public static RWFile<Ran> ranFile() {
        return new RanFile(FS, PARSER);
    }

    public static ROFile<Migrations> migrationsFile() {
        return new MigrationsFile(FS, PARSER);
    }

    public static Ran.Item ranItem(String fileName, Instant on) {
        return new JsonsRan.Item(fileName, on.toString());
    }

    private Jsons() {
    }

    public static interface ROFile<T> {
        T read() throws Io.Err;
    }

    public static interface RWFile<T> extends ROFile<T> {
        void write(T data) throws Io.Err, Json.Err;
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
}
