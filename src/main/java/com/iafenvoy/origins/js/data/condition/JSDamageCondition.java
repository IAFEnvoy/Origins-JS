package com.iafenvoy.origins.js.data.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.util.JSUtil;
import com.iafenvoy.origins.js.util.function.TriPredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record JSDamageCondition(String callbackId, JsonObject params) implements DamageCondition {
    private static final Map<String, TriPredicate<DamageSource, Float, JsonObject>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSDamageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSDamageCondition::callbackId),
            JSUtil.JSON_CODEC.optionalFieldOf("params", new JsonObject()).forGetter(JSDamageCondition::params)
    ).apply(instance, JSDamageCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        TriPredicate<DamageSource, Float, JsonObject> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(source, amount, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS damage condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS damage condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, TriPredicate<DamageSource, Float, JsonObject> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
