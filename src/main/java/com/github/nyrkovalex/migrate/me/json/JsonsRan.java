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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class JsonsRan implements Jsons.Ran {

    public static Item run(String file) {
        return new Item(file, Instant.now().toString());
    }

    public static JsonsRan empty() {
        return new JsonsRan(Collections.emptyList());
    }

    private final List<JsonsRan.Item> ran;

    JsonsRan(List<JsonsRan.Item> items) {
        this.ran = Collections.unmodifiableList(items);
    }

    List<Item> items() {
        return ran;
    }

    @Override
    public boolean canRun(String file) {
        return !ran.stream().anyMatch(r -> r.file().equals(file));
    }

    @Override
    public Jsons.Ran addAll(List<Jsons.Ran.Item> executed) {
        ArrayList<JsonsRan.Item> ranCopy = new ArrayList<>(ran);
        executed.forEach(e -> ranCopy.add(Item.fromOther(e)));
        return new JsonsRan(ranCopy);
    }

    public static class Item implements Jsons.Ran.Item {

        static Item fromOther(Jsons.Ran.Item other) {
            if (other instanceof Item) {
                return (Item) other;
            }
            else {
                return new Item(other.file(), other.on().toString());
            }
        }

        private final String file;
        private final String on;

        Item(String file, String on) {
            this.file = file;
            this.on = on;
        }

        @Override
        public int hashCode() {
            return Objects.hash(file, on);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Item other = (Item) obj;
            return Objects.equals(this.file, other.file)
                    && Objects.equals(this.on, other.on);
        }

        @Override
        public Instant on() {
            return Instant.parse(on);
        }

        @Override
        public String file() {
            return file;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(ran);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JsonsRan other = (JsonsRan) obj;
        return Objects.equals(this.ran, other.ran);
    }
}
