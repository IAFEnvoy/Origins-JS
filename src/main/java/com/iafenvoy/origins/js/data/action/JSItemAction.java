package com.iafenvoy.origins.js.data.action;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record JSItemAction(String callbackId) implements ItemAction {
    private static final Map<String, TriConsumer<Level, Entity, SlotAccess>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSItemAction::callbackId)
    ).apply(instance, JSItemAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity entity, @NotNull SlotAccess slotAccess) {
        TriConsumer<Level, Entity, SlotAccess> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(level, entity, slotAccess);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS item action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS item action '{}'", this.callbackId);
    }

    public static void register(String id, TriConsumer<Level, Entity, SlotAccess> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
