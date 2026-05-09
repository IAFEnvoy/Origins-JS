package com.iafenvoy.origins.js.binding;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.data.action.JSBiEntityAction;
import com.iafenvoy.origins.js.data.action.JSBlockAction;
import com.iafenvoy.origins.js.data.action.JSEntityAction;
import com.iafenvoy.origins.js.data.action.JSItemAction;
import com.iafenvoy.origins.js.data.condition.*;
import com.iafenvoy.origins.js.data.power.JSPower;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class OriginsJSBindings {
    @Info("Get the OriginDataHolder wrapper for an entity.")
    public static HolderWrapper getHolder(Entity entity) {
        if (entity == null) return null;
        try {
            return new HolderWrapper(OriginDataHolder.get(entity));
        } catch (Exception e) {
            OriginsJS.LOGGER.debug("[OriginsJS] Failed to get OriginDataHolder for: {}", entity, e);
        }
        return null;
    }

    @Info("Get the OriginDataHolder wrapper for a player.")
    public static HolderWrapper getPlayerHolder(Player player) {
        return getHolder(player);
    }

    @Info("Grant a power to an entity.")
    public static void grantPower(Entity entity, String source, String powerId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.grantPower(source, powerId);
    }

    @Info("Revoke a power from an entity.")
    public static void revokePower(Entity entity, String source, String powerId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.revokePower(source, powerId);
    }

    @Info("Check if an entity has a power.")
    public static boolean hasPower(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.hasPower(powerId);
    }

    @Info("Set an origin for an entity.")
    public static void setOrigin(Entity entity, String layerId, String originId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.setOrigin(layerId, originId);
    }

    @Info("Check if an entity has an origin.")
    public static boolean hasOrigin(Entity entity, String originId) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.hasOrigin(originId);
    }

    // ========== Actions ==========

    @Info("Register entity action. JSON: origins_js:js_entity_action")
    public static void registerEntityAction(String id, Consumer<Entity> action) {
        JSEntityAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered entity action: {}", id);
    }

    @Info("Register block action. JSON: origins_js:js_block_action")
    public static void registerBlockAction(String id, TriConsumer<Level, BlockPos, Optional<Direction>> action) {
        JSBlockAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered block action: {}", id);
    }

    @Info("Register item action. JSON: origins_js:js_item_action")
    public static void registerItemAction(String id, TriConsumer<Level, Entity, SlotAccess> action) {
        JSItemAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered item action: {}", id);
    }

    @Info("Register bi-entity action. JSON: origins_js:js_bientity_action")
    public static void registerBiEntityAction(String id, BiConsumer<Entity, Entity> action) {
        JSBiEntityAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered bi-entity action: {}", id);
    }

    // ========== Conditions ==========

    @Info("Register entity condition. JSON: origins_js:js_entity_condition")
    public static void registerEntityCondition(String id, Predicate<Entity> predicate) {
        JSEntityCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered entity condition: {}", id);
    }

    @Info("Register block condition. JSON: origins_js:js_block_condition")
    public static void registerBlockCondition(String id, BiPredicate<Level, BlockPos> predicate) {
        JSBlockCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered block condition: {}", id);
    }

    @Info("Register item condition. JSON: origins_js:js_item_condition")
    public static void registerItemCondition(String id, BiPredicate<Level, ItemStack> predicate) {
        JSItemCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered item condition: {}", id);
    }

    @Info("Register bi-entity condition. JSON: origins_js:js_bientity_condition")
    public static void registerBiEntityCondition(String id, BiPredicate<Entity, Entity> predicate) {
        JSBiEntityCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered bi-entity condition: {}", id);
    }

    @Info("Register biome condition. JSON: origins_js:js_biome_condition")
    public static void registerBiomeCondition(String id, BiPredicate<Holder<Biome>, BlockPos> predicate) {
        JSBiomeCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered biome condition: {}", id);
    }

    @Info("Register damage condition. JSON: origins_js:js_damage_condition")
    public static void registerDamageCondition(String id, BiPredicate<DamageSource, Float> predicate) {
        JSDamageCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered damage condition: {}", id);
    }

    @Info("Register fluid condition. JSON: origins_js:js_fluid_condition")
    public static void registerFluidCondition(String id, Predicate<FluidState> predicate) {
        JSFluidCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered fluid condition: {}", id);
    }

    // ========== Powers ==========

    @Info("Return a PowerBuilder for the given id. Call .grant/.revoke/.tick/.active/.inactive/.isActive() then .register().")
    public static JSPower.PowerBuilder powerBuilder(String id) {
        return JSPower.forId(id);
    }

    // ========== Utility ==========

    @Info("Clear all JS callbacks (called on /kubejs reload).")
    public static void clearAll() {
        JSEntityAction.clear();
        JSBlockAction.clear();
        JSItemAction.clear();
        JSBiEntityAction.clear();
        JSEntityCondition.clear();
        JSBlockCondition.clear();
        JSItemCondition.clear();
        JSBiEntityCondition.clear();
        JSBiomeCondition.clear();
        JSDamageCondition.clear();
        JSFluidCondition.clear();
        JSPower.clear();
        OriginsJS.LOGGER.info("[OriginsJS] Cleared all JS callbacks");
    }
}
