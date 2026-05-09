package com.iafenvoy.origins.js.data.action;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.util.JSUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public record JSEntityAction(String callbackId, JsonObject params) implements EntityAction {
    private static final Map<String, BiConsumer<Entity, JsonObject>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSEntityAction::callbackId),
            JSUtil.JSON_CODEC.optionalFieldOf("params", new JsonObject()).forGetter(JSEntityAction::params)
    ).apply(instance, JSEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity entity) {
        BiConsumer<Entity, JsonObject> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(entity, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS entity action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS entity action '{}'", this.callbackId);
    }

    public static void register(String id, BiConsumer<Entity, JsonObject> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
