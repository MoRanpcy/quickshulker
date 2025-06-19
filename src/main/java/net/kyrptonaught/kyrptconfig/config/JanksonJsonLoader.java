package net.kyrptonaught.kyrptconfig.config;

import net.kyrptonaught.jankson.Jankson;

import java.io.InputStream;

public class JanksonJsonLoader implements JsonLoader {
    private Jankson jankson;

    public void provideJankson(Jankson jankson) {
        this.jankson = jankson;
    }

    public Jankson getJankson() {
        return jankson;
    }

    @Override
    public AbstractConfigFile loadFromString(String input, Class<? extends AbstractConfigFile> output) throws Exception {
        return jankson.fromJson(input, output);
    }

    @Override
    public AbstractConfigFile loadFromInputStream(InputStream input, Class<? extends AbstractConfigFile> output) throws Exception {
        return jankson.fromJson(jankson.load(input), output);
    }

    @Override
    public String toString(AbstractConfigFile config) {
        return jankson.toJson(config).toJson(true, true);
    }
}
