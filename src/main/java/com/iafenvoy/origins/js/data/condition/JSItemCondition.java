package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSItemCondition(String callbackId) implements ItemCondition {
    private static final Map<String, BiPredicate<Level, ItemStack>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSItemCondition::callbackId)
    ).apply(instance, JSItemCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        BiPredicate<Level, ItemStack> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(level, stack);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS item condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS item condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<Level, ItemStack> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
