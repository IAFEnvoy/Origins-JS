package com.iafenvoy.origins.js;

import com.iafenvoy.origins.js.binding.OriginsJSBindings;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

/**
 * KubeJS Plugin for Origins NeoForge integration.
 * <p>
 * Registered via src/main/resources/kubejs.plugins.txt
 */
public class OriginsJSKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        OriginsJS.LOGGER.info("[OriginsJS] Initializing KubeJS plugin");
    }

    @Override
    public void registerBindings(BindingRegistry registry) {
        registry.add("OriginsJS", OriginsJSBindings.class);
    }

    @Override
    public void clearCaches() {
        OriginsJSBindings.clearAll();
    }
}
