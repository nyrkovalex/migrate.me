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

import com.github.nyrkovalex.migrate.me.JsonsRan;
import org.junit.Test;
import com.github.nyrkovalex.seed.Expect;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.mockito.Mock;

public class JsonsRanTest extends Expect.Test {

    @Mock JsonsRan.Item first;
    @Mock JsonsRan.Item second;

    List<JsonsRan.Item> items;
    JsonsRan ran;

    @Before
    public void setUp() throws Exception {
        items = Arrays.asList(first);
        ran = new JsonsRan(items);
    }

    @Test
    public void testShouldCreateRanObjectFromItems() throws Exception {
        expect(ran.items()).toBe(items);
    }

    @Test
    public void testShouldCreateNewObjectAddingAllItems() throws Exception {
        JsonsRan newOne = (JsonsRan) ran.addAll(Arrays.asList(second));
        expect(newOne.items()).toBe(Arrays.asList(first, second));
    }

    @Test
    public void testShouldAllowToRunNewScript() throws Exception {
        given(first.file()).returns("old");
        expect(ran.canRun("new")).toBe(true);
    }

    @Test
    public void testShouldDenyToRunOldScript() throws Exception {
        given(first.file()).returns("old");
        expect(ran.canRun("old")).toBe(false);
    }
}
