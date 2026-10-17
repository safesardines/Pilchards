package com.safesardines.pilchards.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PilchardsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = Platform.getConfigFolder().resolve("pilchards.json");

    private static float armorGlintStrength = 1.0F;

    private PilchardsConfig() {}

    public static void load() {
        Data data = read();
        if (data == null) {
            data = new Data();
            write(data);
        }
        armorGlintStrength = data.armorGlintStrength < 0.0F ? 0.0F : data.armorGlintStrength;
    }

    public static float armorGlintStrength() {
        return armorGlintStrength;
    }

    private static Data read() {
        if (!Files.exists(PATH)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            return GSON.fromJson(reader, Data.class);
        } catch (RuntimeException | IOException e) {
            return null;
        }
    }

    private static void write(Data data) {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
        }
    }

    private static final class Data {
        private float armorGlintStrength = 1.0F;
    }
}
