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

package net.kyrptonaught.jankson.impl;

import net.kyrptonaught.jankson.Jankson;
import net.kyrptonaught.jankson.JsonPrimitive;
import net.kyrptonaught.jankson.api.SyntaxError;

import java.util.Locale;

public class StringParserContext implements ParserContext<JsonPrimitive> {
    private static final String HEX_DIGITS = "0123456789abcdefABCDEF";
    private final int quote;
    private boolean escape = false;
    private int unicodeUs = 0;
    private final StringBuilder builder = new StringBuilder();
    private boolean complete = false;
    private String unicodeSequence = "";

    public StringParserContext(int quote) {
        this.quote = quote;
    }

    @Override
    public boolean consume(int codePoint, Jankson loader) {
        //if (codePoint=='\n') { //At any point, if we've reached the end of the line without an end-quote, terminate to limit the damage.
        //	complete = true;
        //	return false;
        //}

        if (escape) {
            if (unicodeUs > 0) {
                if (codePoint == 'u' || codePoint == 'U') {
                    unicodeUs++;
                    return true;
                } else {
                    if (HEX_DIGITS.indexOf(codePoint) != -1) {
                        //consume the char and emit the sequence if needed
                        unicodeSequence += (char) codePoint;
                        if (unicodeSequence.length() == 4) {
                            emitUnicodeSequence(loader);
                            escape = false;
                        }
                        return true;
                    } else {
                        //don't consume the char, but emit the sequence immediately and take us out of escape mode
                        emitUnicodeSequence(loader);
                        escape = false;
                        return false;
                    }
                }
            }

            escape = false;
            switch (codePoint) {


                case 'b':
                    builder.append('\b');
                    return true;
                case 'f':
                    builder.append('\f');
                    return true;
                case 'n':  // regular \n
                    builder.append('\n');
                    return true;
                case '\n': // JSON5 multiline string
                    return true;
                case 'r':
                    builder.append('\r');
                    return true;
                case 't':
                    builder.append('\t');
                    return true;
                case '"':
                    builder.append('"');
                    return true;
                case '\'':
                    builder.append('\'');
                    return true;
                case '\\':
                    builder.append('\\');
                    return true;
                case 'u':
                case 'U':
                    escape = true;
                    unicodeUs = 1;
                    return true;
                default:
                    builder.append((char) codePoint);
                    return true;
            }
        } else {
            if (codePoint == quote) {
                complete = true;
                return true;
            }

            if (codePoint == '\\') {
                escape = true;
                return true;
            }

            if (codePoint == '\n') { //non-escaped CR. Terminate the string to limit the damage.
                complete = true;
                return false;
            }

            if (codePoint < 0xFFFF) {
                builder.append((char) codePoint);
                return true;
            } else {
                //Construct a high and low surrogate pair for this code point

                int temp = codePoint - 0x10000;
                int highSurrogate = (temp >>> 10) + 0xD800;
                int lowSurrogate = (temp & 0b11_1111_1111) + 0xDC00;

                builder.append((char) highSurrogate);
                builder.append((char) lowSurrogate);

                return true;
            }
        }
    }

    private void emitUnicodeSequence(Jankson loader) {
        if (unicodeUs > 1) {
            unicodeUs--;
            builder.append("\\");
            for (int i = 0; i < unicodeUs; i++) builder.append('u');
            while (unicodeSequence.length() < 4)
                unicodeSequence = "0" + unicodeSequence; //TODO: THIS IS A QUIRK. CONSIDER THROWING INSTEAD
            builder.append(unicodeSequence.toLowerCase(Locale.ROOT));
        } else {
            //we unbox and cast all the way from Long to int because parseInt has some problems with the top bit being set
            int sequence = (int) Long.parseLong(unicodeSequence, 16); //TODO: Also part of the quirk; consider checking the character count
            char[] chars = Character.toChars(sequence); //poor man's conversion to surrogate pair if needed
            for (char ch : chars) builder.append(ch);
        }

        unicodeUs = 0;
        unicodeSequence = "";
        escape = false;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public JsonPrimitive getResult() {
        return JsonPrimitive.of(builder.toString());
    }

    @Override
    public void eof() throws SyntaxError {
        throw new SyntaxError("Expected to find '" + ((char) quote) + "' to end a String, found EOF instead.");
    }
}
