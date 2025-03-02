package com.rinko1231.rangeddamagelimit.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ModConfigManager2 {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/RangedDamageLimit-Falloff.json");
    private static final List<FalloffRule> rule0 = new ArrayList<>();

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                FalloffRule[] loadedRule0 = GSON.fromJson(reader, FalloffRule[].class);
                rule0.clear();
                if (loadedRule0 != null) {
                    rule0.addAll(Arrays.asList(loadedRule0));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() throws IOException {
        List<FalloffRule> defaultRule0 = new ArrayList<>();

        FalloffRule exampleRule0 = new FalloffRule();
        exampleRule0.setFalloff(0.05);

        defaultRule0.add(exampleRule0);

        Files.createDirectories(CONFIG_PATH.getParent());

        // 写入默认配置
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(defaultRule0,writer);
        }
    }


    public static Optional<FalloffRule> getRule0() {
        return rule0.stream()
                .findAny();
    }


}
