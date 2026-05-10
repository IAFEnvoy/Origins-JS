package com.iafenvoy.origins.js.data.power;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.util.JSUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class JSPower extends Power {
    private static final Map<String, Map<String, BiConsumer<OriginDataHolder, JsonObject>>> CALLBACKS = new ConcurrentHashMap<>();
    private static final Map<String, BiPredicate<OriginDataHolder, JsonObject>> IS_ACTIVE = new ConcurrentHashMap<>();
    public static final MapCodec<JSPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.STRING.fieldOf("id").forGetter(p -> p.callbackId),
            JSUtil.JSON_CODEC.optionalFieldOf("params", new JsonObject()).forGetter(p -> p.params)
    ).apply(instance, JSPower::new));
    private final String callbackId;
    private final JsonObject params;

    public JSPower(BaseSettings settings, String callbackId, JsonObject params) {
        super(settings);
        this.callbackId = callbackId;
        this.params = params;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull OriginDataHolder holder) {
        Map<String, BiConsumer<OriginDataHolder, JsonObject>> m = CALLBACKS.get(this.callbackId);
        BiConsumer<OriginDataHolder, JsonObject> cb = m != null ? m.get("grant") : null;
        if (cb != null) try {
            cb.accept(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' grant", this.callbackId, e);
        }
    }

    @Override
    public void revoke(@NotNull OriginDataHolder holder) {
        Map<String, BiConsumer<OriginDataHolder, JsonObject>> m = CALLBACKS.get(this.callbackId);
        BiConsumer<OriginDataHolder, JsonObject> cb = m != null ? m.get("revoke") : null;
        if (cb != null) try {
            cb.accept(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' revoke", this.callbackId, e);
        }
    }

    @Override
    public void tick(@NotNull OriginDataHolder holder) {
        Map<String, BiConsumer<OriginDataHolder, JsonObject>> m = CALLBACKS.get(this.callbackId);
        BiConsumer<OriginDataHolder, JsonObject> cb = m != null ? m.get("tick") : null;
        if (cb != null) try {
            cb.accept(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' tick", this.callbackId, e);
        }
        else super.tick(holder);
    }

    @Override
    public boolean isActive(@NotNull OriginDataHolder holder) {
        BiPredicate<OriginDataHolder, JsonObject> cb = IS_ACTIVE.get(this.callbackId);
        if (cb != null) try {
            return cb.test(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' isActive", this.callbackId, e);
        }
        return super.isActive(holder);
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        Map<String, BiConsumer<OriginDataHolder, JsonObject>> m = CALLBACKS.get(this.callbackId);
        BiConsumer<OriginDataHolder, JsonObject> cb = m != null ? m.get("active") : null;
        if (cb != null) try {
            cb.accept(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' active", this.callbackId, e);
        }
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        Map<String, BiConsumer<OriginDataHolder, JsonObject>> m = CALLBACKS.get(this.callbackId);
        BiConsumer<OriginDataHolder, JsonObject> cb = m != null ? m.get("inactive") : null;
        if (cb != null) try {
            cb.accept(holder, this.params);
        } catch (Exception e) {
            OriginsJS.LOGGER.error("[OriginsJS] Error in JS power '{}' inactive", this.callbackId, e);
        }
    }

    public String getCallbackId() {
        return this.callbackId;
    }

    private static void putCallback(String id, String type, BiConsumer<OriginDataHolder, JsonObject> cb) {
        CALLBACKS.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(type, cb);
    }

    public static void clear() {
        CALLBACKS.clear();
        IS_ACTIVE.clear();
    }

    public static PowerBuilder forId(String id) {
        return new PowerBuilder(id);
    }

    @SuppressWarnings("unused")
    public static final class PowerBuilder {
        private final String id;
        private BiConsumer<OriginDataHolder, JsonObject> grant, revoke, tick, active, inactive;
        private BiPredicate<OriginDataHolder, JsonObject> isActive;

        private PowerBuilder(String id) {
            this.id = id;
        }

        public PowerBuilder grant(BiConsumer<OriginDataHolder, JsonObject> cb) {
            this.grant = cb;
            return this;
        }

        public PowerBuilder revoke(BiConsumer<OriginDataHolder, JsonObject> cb) {
            this.revoke = cb;
            return this;
        }

        public PowerBuilder tick(BiConsumer<OriginDataHolder, JsonObject> cb) {
            this.tick = cb;
            return this;
        }

        public PowerBuilder active(BiConsumer<OriginDataHolder, JsonObject> cb) {
            this.active = cb;
            return this;
        }

        public PowerBuilder inactive(BiConsumer<OriginDataHolder, JsonObject> cb) {
            this.inactive = cb;
            return this;
        }

        public PowerBuilder isActive(BiPredicate<OriginDataHolder, JsonObject> cb) {
            this.isActive = cb;
            return this;
        }

        public void register() {
            if (this.grant != null) putCallback(this.id, "grant", this.grant);
            if (this.revoke != null) putCallback(this.id, "revoke", this.revoke);
            if (this.tick != null) putCallback(this.id, "tick", this.tick);
            if (this.active != null) putCallback(this.id, "active", this.active);
            if (this.inactive != null) putCallback(this.id, "inactive", this.inactive);
            if (this.isActive != null) IS_ACTIVE.put(this.id, this.isActive);
            OriginsJS.LOGGER.info("[OriginsJS] Registered power: {}", this.id);
        }
    }
}
