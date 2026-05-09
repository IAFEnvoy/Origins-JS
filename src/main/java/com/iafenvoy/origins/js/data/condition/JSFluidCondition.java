package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public record JSFluidCondition(String callbackId) implements FluidCondition {
    private static final Map<String, Predicate<FluidState>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSFluidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSFluidCondition::callbackId)
    ).apply(instance, JSFluidCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        Predicate<FluidState> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(state);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS fluid condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS fluid condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, Predicate<FluidState> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
