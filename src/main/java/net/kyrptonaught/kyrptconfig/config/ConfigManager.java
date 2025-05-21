package net.kyrptonaught.kyrptconfig.config;

import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.jankson.Jankson;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ConfigManager {
    protected JsonLoader JANKSON;
    protected final HashMap<String, ConfigStorage> configs = new HashMap<>();
    protected Path dir;
    protected String MOD_ID;

    private ConfigManager(String mod_id) {
        this.MOD_ID = mod_id;
        dir = FabricLoader.getInstance().getConfigDir();
        buildJankson();
    }

    public void buildJankson() {
        JANKSON = new JanksonJsonLoader();
        Jankson.Builder builder = CustomJankson.customJanksonBuilder();
        setJANKSON(builder
                .registerSerializer(Identifier.class, (identifier, marshaller) -> marshaller.serialize(identifier.toString()))
                .registerDeserializer(String.class, Identifier.class, (s, m) -> Identifier.of(s))
                .build());
    }

    public AbstractConfigFile getConfig(String name) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        return configs.get(name).config;
    }

    public AbstractConfigFile getConfigDefault(String name) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        return configs.get(name).getDefaultConfig();
    }

    public void registerFile(String name, AbstractConfigFile defaultConfig) {
        if (JANKSON == null) buildJankson();
        registerFile(name, defaultConfig, JANKSON);
    }

    public void registerFile(String name, AbstractConfigFile defaultConfig, JsonLoader jsonLoader) {
        if (!name.endsWith(".json5")) name = name + ".json5";
        configs.put(name, new ConfigStorage(dir.resolve(name), defaultConfig, jsonLoader));
    }

    public void save() {
        configs.values().forEach(configStorage -> configStorage.save(MOD_ID));
    }

    public void load() {
        configs.values().forEach(configStorage -> configStorage.load(MOD_ID));
        save();
    }

    public Jankson getJANKSON() {
        return ((JanksonJsonLoader) JANKSON).getJankson();
    }

    public void setJANKSON(Jankson jankson) {
        ((JanksonJsonLoader) JANKSON).provideJankson(jankson);
    }

    public static class SingleConfigManager extends ConfigManager {
        public SingleConfigManager(String mod_id, AbstractConfigFile defaultConfig) {
            super(mod_id);
            registerFile(mod_id + "config", defaultConfig);
        }

        public AbstractConfigFile getConfig() {
            return getConfig(MOD_ID + "config");
        }

        public AbstractConfigFile getConfigDefault() {
            return getConfigDefault(MOD_ID + "config");
        }
    }

    public static class MultiConfigManager extends ConfigManager {
        public MultiConfigManager(String mod_id) {
            super(mod_id);
            dir = Path.of(dir + "/" + MOD_ID);
            if (!Files.exists(dir)) {
                try {
                    Files.createDirectories(dir);
                } catch (IOException ignored) {
                }
            }
        }

        public void load(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).load(MOD_ID);
            save(config);
        }

        public void save(String config) {
            if (!config.endsWith(".json5")) config = config + ".json5";
            this.configs.get(config).save(MOD_ID);
        }
    }

}