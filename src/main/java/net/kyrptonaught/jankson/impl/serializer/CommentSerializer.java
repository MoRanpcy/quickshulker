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

package net.kyrptonaught.jankson.impl.serializer;

import net.kyrptonaught.jankson.JsonGrammar;

import java.io.IOException;
import java.io.Writer;

public class CommentSerializer {

    public static void print(Writer writer, String comment, int indent, JsonGrammar grammar) throws IOException {
        if (comment == null || comment.trim().isEmpty()) return;
        StringBuilder b = new StringBuilder(comment.length());
        print(b, comment, indent, grammar);
        writer.append(b);
    }

    public static void print(StringBuilder builder, String comment, int indent, JsonGrammar grammar) {
        boolean comments = grammar.hasComments();
        boolean whitespace = grammar.shouldOutputWhitespace();
        print(builder, comment, indent, comments, whitespace);
    }

    // Note: Indent may be -1.
    public static void print(StringBuilder builder, String comment, int indent, boolean comments, boolean whitespace) {
        if (!comments) return;
        if (comment == null || comment.trim().isEmpty()) return;

        if (whitespace) {
            if (comment.contains("\n")) {
                //Use /* */ comment
                builder.append("/* ");
                String[] lines = comment.split("\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (i != 0) builder.append("   ");
                    builder.append(line);
                    builder.append('\n');
                    for (int j = 0; j < indent + 1; j++) {
                        builder.append('\t');
                    }
                }
                builder.append("*/\n");
                for (int i = 0; i < indent + 1; i++) {
                    builder.append('\t');
                }
            } else {
                //Use a single-line comment
                builder.append("// ");
                builder.append(comment);
                builder.append('\n');
                for (int i = 0; i < indent + 1; i++) {
                    builder.append('\t');
                }
            }
        } else {
            //Always use /* */ comments

            if (comment.contains("\n")) {
                //Split the lines into separate /* */ comments and string them together inline.

                String[] lines = comment.split("\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    builder.append("/* ");
                    builder.append(line);
                    builder.append(" */ ");
                }
            } else {
                builder.append("/* ");
                builder.append(comment);
                builder.append(" */ ");
            }
        }
    }
}
