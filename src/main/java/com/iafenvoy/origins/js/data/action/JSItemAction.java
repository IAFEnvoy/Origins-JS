package com.iafenvoy.origins.js.data.action;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.util.JSUtil;
import com.iafenvoy.origins.js.util.function.QuadConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record JSItemAction(String callbackId, JsonObject params) implements ItemAction {
    private static final Map<String, QuadConsumer<Level, Entity, SlotAccess, JsonObject>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSItemAction::callbackId),
            JSUtil.JSON_CODEC.optionalFieldOf("params", new JsonObject()).forGetter(JSItemAction::params)
    ).apply(instance, JSItemAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity entity, @NotNull SlotAccess slotAccess) {
        QuadConsumer<Level, Entity, SlotAccess, JsonObject> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(level, entity, slotAccess, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS item action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS item action '{}'", this.callbackId);
    }

    public static void register(String id, QuadConsumer<Level, Entity, SlotAccess, JsonObject> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
