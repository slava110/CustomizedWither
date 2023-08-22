package com.slava_110.customizedwither;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

@Config(modid = "customizedwither")
public class CustomizedWitherConfig {

    public static GeneralConfig general = new GeneralConfig();

    public static SummoningConfig summoning = new SummoningConfig();

    public static SkullConfig skullConfig = new SkullConfig();

    public static class GeneralConfig {

        @Config.Comment("Max wither heatlh")
        public double maxHealth = 300.0;

        @Config.Comment("Health percentage on which wither gets his armor")
        public float secondStageHealthPercentage = 0.5F;

        @Config.Comment("Movement speed")
        public double movementSpeed = 0.6000000238418579;

        public double followRange = 40.0;

        @Config.Comment("If wither is flying he'll keep sqrt(X) blocks away from the player")
        public double distanceToPlayer = 9.0;

        @Config.Comment("If wither is flying he'll try to stay on this height above the player's level")
        public double flyingHeight = 5.0;

        @Config.Comment("Wither armor points")
        public double armor = 4.0;

        @Config.Comment("Blocks wither can destroy with its body")
        public String[] destroyableBlocksWhitelist = new String[0];

        @Config.Comment("Blocks wither cannot destroy with its body")
        public String[] destroyableBlocksBlacklist = new String[]{
                "minecraft:bedrock",
                "minecraft:end_portal",
                "minecraft:end_portal_frame",
                "minecraft:command_block",
                "minecraft:repeating_command_block",
                "minecraft:chain_command_block",
                "minecraft:structure_block",
                "minecraft:structure_void",
                "minecraft:piston_extension",
                "minecraft:end_gateway"
        };
    }

    public static class SummoningConfig {

        @Config.Comment("Amount of ticks for which wither will be invulnerable at spawn")
        public int invulnerabilityTime = 220;

        @Config.Comment("Initial amount of health at spawn. By default around 30%")
        public float initialHealthPercentage = 0.33F;

        @Config.Comment("If wither should make an explosion after charging")
        public boolean enableExplosion = true;

        @Config.Comment("Charging explosion strength")
        public float explosionStrength = 1.0F;

        @Config.Comment("If charging explosion should make fire")
        public boolean explosionCausesFire = false;

        @Config.Comment("If charging explosion should damage terrain")
        public boolean explosionDamagesTerrain = false;
    }

    public static class SkullConfig {
        @Config.Comment("How much damage wither skull makes while wither is alive (is he a vampire? :o )")
        public float damageWitherAlive = 8.0F;

        @Config.Comment("How much damage wither skull makes if wither is already dead")
        public float damageWitherDead = 5.0F;

        @Config.Comment("How much health wither will regenerate when he kills a mob")
        public float witherHealOnKill = 5.0F;

        @Config.Comment("Allow players to return wither skull to the wither like a ghast fireball")
        public boolean allowReturningToTheSender = false;

        @Config.Comment("Enable skull explosion on hit")
        public boolean enableExplosion = true;

        public float explosionStrength = 1.0F;

        public boolean explosionCausesFire = false;

        public boolean explosionDamagesTerrain = true;

        @Config.Comment({
                "Potion effects from wither skull on easy difficulty",
                "Format: `<effect id>;<time>;<amplifier>`"
        })
        public String[] effectsEasy = new String[0];

        @Config.Comment({
                "Potion effects from wither skull on normal difficulty",
                "Format: `<effect id>;<time>;<amplifier>`"
        })
        public String[] effectsNormal = new String[]{"minecraft:wither;10;1"};

        @Config.Comment({
                "Potion effects from wither skull on hard difficulty",
                "Format: `<effect id>;<time>;<amplifier>`"
        })
        public String[] effectsHard = new String[]{"minecraft:wither;40;1"};

        private final EnumMap<EnumDifficulty, List<PotionEffect>> cachedEffects = new EnumMap<>(EnumDifficulty.class);

        public List<PotionEffect> getEffectsForDifficulty(EnumDifficulty difficulty) {
            List<PotionEffect> effects = cachedEffects.get(difficulty);
            if (effects != null) {
                return effects;
            } else {
                return Collections.emptyList();
            }
        }

        public void parseEffectsIntoCache() {
            cachedEffects.clear();
            cachedEffects.put(EnumDifficulty.EASY, parseEffects(effectsEasy));
            cachedEffects.put(EnumDifficulty.NORMAL, parseEffects(effectsNormal));
            cachedEffects.put(EnumDifficulty.HARD, parseEffects(effectsHard));
        }

        private List<PotionEffect> parseEffects(String[] rawEffects) {
            List<PotionEffect> effects = new ArrayList<>(rawEffects.length);
            for(String rawEffect : rawEffects) {
                String[] parts = rawEffect.split(";", 3);
                Potion potion;
                try {
                    potion = Potion.getPotionFromResourceLocation(parts[0]);
                } catch (Exception e) {
                    CustomizedWitherMod.logger.warn("Unable to find potion effect {} in skull potion effects", rawEffect);
                    continue;
                }

                int duration = 200;
                if(parts.length > 1) {
                    try {
                        duration = Integer.parseInt(parts[1]) * 20;
                    } catch (Exception e) {
                        CustomizedWitherMod.logger.warn("Unable to parse duration {} in skull potion effects", rawEffect);
                        continue;
                    }
                }

                int amplifier = 1;
                if(parts.length > 2) {
                    try {
                        amplifier = Integer.parseInt(parts[2]);
                    } catch (Exception e) {
                        CustomizedWitherMod.logger.warn("Unable to parse amplifier {} in skull potion effects", rawEffect);
                        continue;
                    }
                }

                effects.add(new PotionEffect(potion, duration, amplifier));
            }
            return effects;
        }
    }

    @Mod.EventBusSubscriber(modid = "customizedwither")
    public static class ConfigListener {

        @SubscribeEvent
        public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent ev) {
            if(ev.getModID().equals("customizedwither")) {
                ConfigManager.sync(ev.getModID(), Config.Type.INSTANCE);
                CustomizedWitherConfig.skullConfig.parseEffectsIntoCache();
            }
        }
    }
}
