package com.iafenvoy.origins.js.data.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.util.JSUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSFluidCondition(String callbackId, JsonObject params) implements FluidCondition {
    private static final Map<String, BiPredicate<FluidState, JsonObject>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSFluidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSFluidCondition::callbackId),
            JSUtil.JSON_CODEC.optionalFieldOf("params", new JsonObject()).forGetter(JSFluidCondition::params)
    ).apply(instance, JSFluidCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        BiPredicate<FluidState, JsonObject> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(state, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS fluid condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS fluid condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<FluidState, JsonObject> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
