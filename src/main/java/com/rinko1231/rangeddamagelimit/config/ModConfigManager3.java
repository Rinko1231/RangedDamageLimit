package com.rinko1231.rangeddamagelimit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.damagesource.DamageSource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraftforge.registries.ForgeRegistries;

public class ModConfigManager3 {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/RangedDamageLimit-BlacklistNoFalloff.json");

    private static final Set<String> entityBlacklist = new HashSet<>();
    // byd mojang居然没给伤害类型安排注册表
    //private static final Set<String> damageTypeBlacklist = new HashSet<>();

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                BlacklistConfig config = GSON.fromJson(reader, BlacklistConfig.class);
                entityBlacklist.clear();

                if (config != null) {
                    if (config.entityBlacklist != null) {
                        entityBlacklist.addAll(config.entityBlacklist);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() throws IOException {
        BlacklistConfig defaultConfig = new BlacklistConfig();

        defaultConfig.entityBlacklist = Arrays.asList(
                "minecraft:ender_dragon",
                "minecraft:wither"
        );


        Files.createDirectories(CONFIG_PATH.getParent());
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(defaultConfig, writer);
        }
    }

    public static boolean isEntityBlacklisted(String entityId) {
        return entityBlacklist.contains(entityId);
    }

    private static class BlacklistConfig {
        List<String> entityBlacklist;
        //List<String> damageTypeBlacklist;
    }
}