package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSDamageCondition(String callbackId) implements DamageCondition {
    private static final Map<String, BiPredicate<DamageSource, Float>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSDamageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSDamageCondition::callbackId)
    ).apply(instance, JSDamageCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        BiPredicate<DamageSource, Float> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(source, amount);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS damage condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS damage condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<DamageSource, Float> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
