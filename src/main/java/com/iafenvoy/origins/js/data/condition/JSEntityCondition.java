package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public record JSEntityCondition(String callbackId) implements EntityCondition {
    private static final Map<String, Predicate<Entity>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSEntityCondition::callbackId)
    ).apply(instance, JSEntityCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Predicate<Entity> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(entity);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS entity condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS entity condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, Predicate<Entity> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
