package com.iafenvoy.origins.js.data.action;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public record JSBiEntityAction(String callbackId) implements BiEntityAction {
    private static final Map<String, BiConsumer<Entity, Entity>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSBiEntityAction::callbackId)
    ).apply(instance, JSBiEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity actor, @NotNull Entity target) {
        BiConsumer<Entity, Entity> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(actor, target);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS bi-entity action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS bi-entity action '{}'", this.callbackId);
    }

    public static void register(String id, BiConsumer<Entity, Entity> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
