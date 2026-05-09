package com.iafenvoy.origins.js.data.condition;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public record JSBiEntityCondition(String callbackId) implements BiEntityCondition {
    private static final Map<String, BiPredicate<Entity, Entity>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSBiEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSBiEntityCondition::callbackId)
    ).apply(instance, JSBiEntityCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity actor, @NotNull Entity target) {
        BiPredicate<Entity, Entity> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            return cb.test(actor, target);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS bi-entity condition '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS bi-entity condition '{}'", this.callbackId);
        return false;
    }

    public static void register(String id, BiPredicate<Entity, Entity> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
