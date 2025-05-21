package net.kyrptonaught.kyrptconfig.config;

import net.kyrptonaught.jankson.JsonElement;
import net.kyrptonaught.jankson.api.DeserializationException;

import java.lang.reflect.Field;

public interface CustomSerializable {

    default JsonElement toJson(CustomMarshaller m) {
        return m.serializeNonCustom(this);
    }

    default CustomSerializable fromJson(CustomMarshaller m, JsonElement obj, Class<CustomSerializable> clazz) throws DeserializationException {
        return m.marshallNonCustom(clazz, obj, false);
    }

    default boolean shouldSerializeField(Field field) {
        return shouldSerializeField(field.getName());
    }

    default boolean shouldSerializeField(String field) {
        return true;
    }
}
