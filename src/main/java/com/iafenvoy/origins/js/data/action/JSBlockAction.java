package com.iafenvoy.origins.js.data.action;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.js.OriginsJS;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public record JSBlockAction(String callbackId) implements BlockAction {
    private static final Map<String, TriConsumer<Level, BlockPos, Optional<Direction>>> CALLBACKS = new ConcurrentHashMap<>();
    public static final MapCodec<JSBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(JSBlockAction::callbackId)
    ).apply(instance, JSBlockAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        TriConsumer<Level, BlockPos, Optional<Direction>> cb = CALLBACKS.get(this.callbackId);
        if (cb != null) try {
            cb.accept(level, pos, direction);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS block action '{}'", this.callbackId, e);
        }
        else OriginsJS.LOGGER.warn("[OriginsJS] Unknown JS block action '{}'", this.callbackId);
    }

    public static void register(String id, TriConsumer<Level, BlockPos, Optional<Direction>> callback) {
        CALLBACKS.put(id, callback);
    }

    public static void clear() {
        CALLBACKS.clear();
    }
}
