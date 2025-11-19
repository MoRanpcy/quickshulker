/*
 * MIT License
 *
 * Copyright (c) 2018-2020 Falkreon (Isaac Ellingson)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.kyrptonaught.jankson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Tagging class for Json objects, arrays, and primitives
 */
public abstract class JsonElement implements Cloneable {
    public abstract JsonElement clone();

    public String toJson() {
        return toJson(false, false, 0);
    }

    public String toJson(boolean comments, boolean newlines) {
        return toJson(comments, newlines, 0);
    }

    @Deprecated
    public abstract String toJson(boolean comments, boolean newlines, int depth);

    public String toJson(JsonGrammar grammar, int depth) {
        StringWriter w = new StringWriter();
        try {
            toJson(w, grammar, depth);
            w.flush();
            return w.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toJson(JsonGrammar grammar) {
        return toJson(grammar, 0);
    }

    public abstract void toJson(Writer writer, JsonGrammar grammar, int depth) throws IOException;
}
