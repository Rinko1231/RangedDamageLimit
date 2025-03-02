package com.rinko1231.rangeddamagelimit;


import com.rinko1231.rangeddamagelimit.config.GlobalConfig;
import com.rinko1231.rangeddamagelimit.config.MobProtectionRule;
import com.rinko1231.rangeddamagelimit.config.ModConfigManager;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

@Mod(RangedDamageLimit.ID)
public class RangedDamageLimit {
    public static final String ID = "rangeddamagelimit";

    public RangedDamageLimit() {
        GlobalConfig.setup();
        ModConfigManager.loadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private double calculateRange(DamageSource damagesource, Entity entity) {
        return damagesource.getEntity() != null ? entity.distanceToSqr(damagesource.getEntity()) : (double) -1.0F;
    }

    // 注册配置重载事件
    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
            ModConfigManager.loadConfig();
        }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void rangeLimit(LivingHurtEvent event) {
        String mobId = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()).toString();
        DamageSource source = event.getSource();
        if (source.getDirectEntity() != null) {
            double range = calculateRange(source, event.getEntity());
            double decay = GlobalConfig.rangeDamageFalloffPerBlock.get();
            float originalAmount = event.getAmount();
            float changedAmount = (float) ((1 - decay * Math.sqrt(range)) * originalAmount);
            event.setAmount(Math.max(0.0f, changedAmount));
            Optional<MobProtectionRule> rule = ModConfigManager.getRuleForMob(mobId);
            if (rule.isPresent()) {
                MobProtectionRule r = rule.get();
                if (range > r.getProtectionDistance() * r.getProtectionDistance() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    if (event.getAmount() >= r.getDamageCap() && r.getDamageCap() > 0)
                        event.setAmount((float) r.getDamageCap());
                }
            }
        }

    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        Entity target = event.getEntity();
        String mobId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType()).toString();

        ModConfigManager.getRuleForMob(mobId).ifPresent(rule -> {

            double distanceSq = calculateRange(event.getSource(), target);
            boolean noAggro = rule.getNoAggroBeyondCertainDistance();
            double noAggroDistanceBasic = Math.max(rule.getNoAggroDistance(), rule.getProtectionDistance());
            double noAggroDistance = noAggroDistanceBasic * noAggroDistanceBasic;

            if (noAggro && distanceSq > noAggroDistance) {

                event.setCanceled(true);
                event.setResult(Event.Result.DENY);

            }
        });
    }


}
