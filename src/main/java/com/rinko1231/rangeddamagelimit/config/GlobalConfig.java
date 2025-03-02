package com.rinko1231.rangeddamagelimit.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class GlobalConfig {
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec.DoubleValue rangeDamageFalloffPerBlock;


    static {
        BUILDER.push("Ranged Damage Config");

        rangeDamageFalloffPerBlock = BUILDER
                .defineInRange("Ranged Damage Falloff Percent Per Block", 0.05, 0.0, 1);

        SPEC = BUILDER.build();
    }

    public static void setup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "RangedDamageLimit-Falloff.toml");
    }

}
