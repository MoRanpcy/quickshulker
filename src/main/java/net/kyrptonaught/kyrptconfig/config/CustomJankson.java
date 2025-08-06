package net.kyrptonaught.kyrptconfig.config;

import net.kyrptonaught.jankson.Jankson;

import java.lang.reflect.Field;

public class CustomJankson {
    public static Jankson.Builder customJanksonBuilder() {
        return new Jankson.Builder(true);
    }

    public static Boolean shouldSerializeField(Object t, Field field) {
        if (t instanceof CustomSerializable customSerializable)
            return customSerializable.shouldSerializeField(field);
        return true;
    }
}
