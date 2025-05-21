package net.kyrptonaught.kyrptconfig.config;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigStorage {
    private final Path saveFile;
    public AbstractConfigFile config;
    private final AbstractConfigFile defaultConfig;
    private final JsonLoader jsonLoader;

    public ConfigStorage(Path fileName, AbstractConfigFile defaultConfig, JsonLoader jsonLoader) {
        this.saveFile = fileName;
        this.defaultConfig = defaultConfig;
        this.jsonLoader = jsonLoader;
    }

    public void save(String MOD_ID) {
        try (OutputStream os = Files.newOutputStream(saveFile); OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            String json = jsonLoader.toString(config);
            out.write(json);
        } catch (Exception e) {
            System.out.println(getConfigName(MOD_ID, "Failed to save #CONFIG"));
            e.printStackTrace();
        }
    }

    public AbstractConfigFile load(String MOD_ID) {
        if (!Files.exists(saveFile) || !Files.isReadable(saveFile)) {
            System.out.println(getConfigName(MOD_ID, "Unable to find #CONFIG! Creating a default config"));
            config = defaultConfig;
            return config;
        }

        boolean failed = false;
        try (InputStream in = Files.newInputStream(saveFile, StandardOpenOption.READ)) {
            config = jsonLoader.loadFromInputStream(in, defaultConfig.getClass());
        } catch (Exception e) {
            failed = true;
            e.printStackTrace();
        }
        if (failed || (config == null)) {
            System.out.println(getConfigName(MOD_ID, "Failed to load #CONFIG! Overwriting with default config"));
            config = defaultConfig;
        }
        return config;
    }

    public AbstractConfigFile getDefaultConfig() {
        return defaultConfig;
    }

    private String getConfigName(String MOD_ID, String message) {
        return "[" + MOD_ID + "]: " + message.replaceAll("#CONFIG", "config: " + saveFile.getFileName().toString());
    }
}