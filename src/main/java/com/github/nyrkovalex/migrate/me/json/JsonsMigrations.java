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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
