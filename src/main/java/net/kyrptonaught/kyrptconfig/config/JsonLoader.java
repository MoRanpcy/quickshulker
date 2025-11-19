package net.kyrptonaught.kyrptconfig.config;


import java.io.InputStream;

public interface JsonLoader {

    AbstractConfigFile loadFromString(String input, Class<? extends AbstractConfigFile> output) throws Exception;

    AbstractConfigFile loadFromInputStream(InputStream input, Class<? extends AbstractConfigFile> output) throws Exception;

    String toString(AbstractConfigFile config) throws Exception;
}
