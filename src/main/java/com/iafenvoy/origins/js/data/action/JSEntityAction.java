package com.iafenvoy.origins.js.data.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public record JSEntityAction(String callbackId) implements EntityAction {
    private static final Map<String, Consumer<Entity>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSEntityAction::callbackId)
    ).apply(instance, JSEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity entity) {
        Consumer<Entity> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(entity);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS entity action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS entity action '{}'", this.callbackId);
    }

    public static void register(String id, Consumer<Entity> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
