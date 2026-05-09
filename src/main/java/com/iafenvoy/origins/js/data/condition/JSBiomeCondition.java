package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSBiomeCondition(String callbackId) implements BiomeCondition {
    private static final Map<String, BiPredicate<Holder<Biome>, BlockPos>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSBiomeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSBiomeCondition::callbackId)
    ).apply(instance, JSBiomeCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
        BiPredicate<Holder<Biome>, BlockPos> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(biome, pos);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS biome condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS biome condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<Holder<Biome>, BlockPos> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
