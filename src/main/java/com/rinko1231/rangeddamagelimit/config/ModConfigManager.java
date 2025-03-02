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

public class ModConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/RangedDamageLimit-Rules.json");
    private static final List<MobProtectionRule> rules = new ArrayList<>();

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                MobProtectionRule[] loadedRules = GSON.fromJson(reader, MobProtectionRule[].class);
                rules.clear();

                if (loadedRules != null) {
                    rules.addAll(Arrays.asList(loadedRules));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() throws IOException {
        List<MobProtectionRule> defaultRules = new ArrayList<>();

        MobProtectionRule exampleRule = new MobProtectionRule();
        exampleRule.setMobId("minecraft:example");
        exampleRule.setProtectionDistance(10.0);
        exampleRule.setDamageCap(1.0);
        exampleRule.setNoAggroBeyondCertainDistance(false);
        exampleRule.setNoAggroDistance(15.0);
        defaultRules.add(exampleRule);

        Files.createDirectories(CONFIG_PATH.getParent());

        // 写入默认配置
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(defaultRules, writer);
        }
    }

    public static Optional<MobProtectionRule> getRuleForMob(String mobId) {
        return rules.stream()
                .filter(rule -> rule.getMobId().equalsIgnoreCase(mobId))
                .findFirst();
    }


}
