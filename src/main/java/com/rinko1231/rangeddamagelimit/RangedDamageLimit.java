package com.rinko1231.rangeddamagelimit;


import com.rinko1231.rangeddamagelimit.config.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
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
        //GlobalConfig.setup();
        ModConfigManager.loadConfig();
        ModConfigManager2.loadConfig();
        ModConfigManager3.loadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private double calculateRange(DamageSource damagesource, Entity target) {
        Entity direct = damagesource.getDirectEntity();
        Entity owner = damagesource.getEntity();

        // 有主的远程投射物
        if (direct != null && ownerOfProjectile(direct) != null) {
            Entity owner1 = ownerOfProjectile(direct);
            Entity owner2 = owner;

            double distance1 = -1.0;
            double distance2 = -1.0;

            //仅当实体在同一个 Level或已加入世界
            if (owner1 != null && owner1.level() == target.level() && owner1.isAddedToWorld()) {
                distance1 = target.distanceToSqr(owner1);
            }
            if (owner2 != null && owner2.level() == target.level() && owner2.isAddedToWorld()) {
                distance2 = target.distanceToSqr(owner2);
            }

            if (distance1 < 0 && distance2 < 0) return -1.0; // 都不可用
            if (distance1 < 0) return distance2;
            if (distance2 < 0) return distance1;
            return Math.min(distance1, distance2);
        }

        if (owner != null && owner.level() == target.level() && owner.isAddedToWorld()) {
            return target.distanceToSqr(owner);
        }

        return -1.0;
    }

    public Entity ownerOfProjectile(Entity entity)
    {
        Entity owner = null;
        if(entity instanceof Projectile projectile) owner = projectile.getOwner();
        if(entity instanceof ThrownPotion thrownPotion) owner = thrownPotion.getOwner();
        if(entity instanceof AreaEffectCloud areaEffectCloud) owner = areaEffectCloud.getOwner();
        return owner;
    }


    // 注册配置重载事件
    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        ModConfigManager2.loadConfig();
        ModConfigManager.loadConfig();
        ModConfigManager3.loadConfig();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void rangeLimit(LivingHurtEvent event) {
        String mobId = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()).toString();
        DamageSource source = event.getSource();


        //检查黑名单
        Entity SourceOwner = event.getSource().getEntity();
        if (SourceOwner!=null && ModConfigManager3.isEntityBlacklisted(ForgeRegistries.ENTITY_TYPES.getKey(SourceOwner.getType()).toString())) {
            return;
        }


        if (source.getDirectEntity() != null) {
            double range = calculateRange(source, event.getEntity());

            Optional<FalloffRule> rule0 = ModConfigManager2.getRule0();
            if (rule0.isPresent() && range >= 0) {
                FalloffRule r0 = rule0.get();
                double decay = r0.getFalloff();
                float originalAmount = event.getAmount();
                float changedAmount = (float) ((1 - decay * Math.sqrt(range)) * originalAmount);
                event.setAmount(Math.max(0.0f, changedAmount));
            }

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

        Entity SourceOwner = event.getSource().getEntity();
        if (SourceOwner!=null && ModConfigManager3.isEntityBlacklisted(ForgeRegistries.ENTITY_TYPES.getKey(SourceOwner.getType()).toString())) {
            return;
        }

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
