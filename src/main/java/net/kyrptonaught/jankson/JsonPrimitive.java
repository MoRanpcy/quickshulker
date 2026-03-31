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

import net.kyrptonaught.jankson.api.Escaper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class JsonPrimitive extends JsonElement {
    /**
     * Convenience instance of json "true". Don't use identity comparison (==) on these! Use equals instead.
     */
    public static JsonPrimitive TRUE = new JsonPrimitive(Boolean.TRUE);
    /**
     * Convenience instance of json "false". Don't use identity comparison (==) on these! Use equals instead.
     */
    public static JsonPrimitive FALSE = new JsonPrimitive(Boolean.FALSE);

    @Nonnull
    private Object value;

    private JsonPrimitive() {
    }

    /**
     * Creates a new JsonPrimitive node representing the passed-in value.
     *
     * <p>Note: This constructor may do expensive type inspection to verify that the passed-in object
     * is well-formed. Please use one of the JsonPrimitive.of(x) static factory variants if possible,
     * because using function polymorphism often winds up validating the results "for free".
     *
     * @param value
     */
    public JsonPrimitive(@Nonnull Object value) {
        if (value instanceof Character) {
            this.value = String.valueOf(value);
        } else if (value instanceof Long) {
            this.value = value;
        } else if (value instanceof Double) {
            this.value = value;
        } else if (value instanceof BigInteger) {
            this.value = ((BigInteger) value).toString(16);
        } else if (value instanceof Float) {
            this.value = Double.valueOf((Float) value);
        } else if (value instanceof Number) {
            this.value = ((Number) value).longValue();
        } else if (value instanceof CharSequence) {
            this.value = value.toString();
        } else if (value instanceof Boolean) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Object of type '" + value.getClass().getCanonicalName() + "' not allowed as a JsonPrimitive");
        }
    }

    @Nonnull
    public String asString() {
        if (value == null) return "null";
        return value.toString();
    }

    public boolean asBoolean(boolean defaultValue) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            return defaultValue;
        }
    }

    public byte asByte(byte defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else {
            return defaultValue;
        }
    }

    public char asChar(char defaultValue) {
        if (value instanceof Number) {
            return (char) ((Number) value).intValue();
        } else if (value instanceof Character) {
            return ((Character) value).charValue();
        } else if (value instanceof String) {
            if (((String) value).length() == 1) {
                return ((String) value).charAt(0);
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public short asShort(short defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else {
            return defaultValue;
        }
    }

    public int asInt(int defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return defaultValue;
        }
    }

    public long asLong(long defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            return defaultValue;
        }
    }

    public float asFloat(float defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            return defaultValue;
        }
    }

    public double asDouble(double defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            return defaultValue;
        }
    }

    public BigInteger asBigInteger(BigInteger defaultValue) {
        if (value instanceof Number) {
            return BigInteger.valueOf(((Number) value).longValue());
        } else if (value instanceof String) {
            return new BigInteger((String) value, 16);
        } else {
            return defaultValue;
        }
    }

    public BigDecimal asBigDecimal(BigDecimal defaultValue) {
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    public String toString() {
        return toJson();
    }

    @Nonnull
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof JsonPrimitive) {
            return Objects.equals(value, ((JsonPrimitive) other).value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toJson(boolean comments, boolean newlines, int depth) {
        return toJson(JsonGrammar.builder().withComments(comments).printWhitespace(newlines).build(), depth);
    }

    @Override
    public void toJson(Writer writer, JsonGrammar grammar, int depth) throws IOException {

        if (value == null) {
            writer.write("null");
            return;
        }

        if (value instanceof Double && grammar.bareSpecialNumerics) {
            double d = ((Double) value).doubleValue();
            if (Double.isNaN(d)) {
                writer.write("NaN");
                return;
            }
            if (Double.isInfinite(d)) {
                if (d < 0) {
                    writer.write("-Infinity");
                    return;
                } else {
                    writer.write("Infinity");
                    return;
                }
            }
            writer.write(value.toString());
            return;
        } else if (value instanceof Number) {
            writer.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            writer.write(value.toString());
            return;
        }

        writer.write('\"');
        writer.write(Escaper.escapeString(value.toString())); //TODO: Configurable unicode blocks to escape?
        writer.write('\"');
    }

    //IMPLEMENTATION for Cloneable
    @Override
    public JsonPrimitive clone() {
        JsonPrimitive result = new JsonPrimitive();
        result.value = this.value;
        return result;
    }

    public static JsonPrimitive of(@Nonnull String s) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = s;
        return result;
    }

    public static JsonPrimitive of(@Nonnull BigInteger n) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = n.toString(16);
        return result;
    }

    public static JsonPrimitive of(@Nonnull BigDecimal n) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = n.toString(); //Appropriate for `new BigDecimal(s)`
        return result;
    }

    public static JsonPrimitive of(@Nonnull Double d) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = d;
        return result;
    }

    public static JsonPrimitive of(@Nonnull Long l) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = l;
        return result;
    }

    public static JsonPrimitive of(@Nonnull Boolean b) {
        JsonPrimitive result = new JsonPrimitive();
        result.value = b;
        return result;
    }
}
