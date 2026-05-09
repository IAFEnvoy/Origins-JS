package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSBlockCondition(String callbackId) implements BlockCondition {
    private static final Map<String, BiPredicate<Level, BlockPos>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSBlockCondition::callbackId)
    ).apply(instance, JSBlockCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        BiPredicate<Level, BlockPos> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(level, pos);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS block condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS block condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<Level, BlockPos> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
