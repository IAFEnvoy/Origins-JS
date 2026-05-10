# Origins JS

This is a KubeJS integration for [Origins (NeoForge)](https://github.com/IAFEnvoy/Origins-NeoForge), allowing you to
manage
origins and powers via KubeJS.

Note: This is my first time to make a KubeJS addon, most code written by DeepSeek and reviewed by my self. So if you
think some APIs are weird or not well-designed, please let me know.

## Features

- **Origin & Power Management**: Grant, revoke, and check origins/powers from JS scripts.
- **Custom Actions**: Define `EntityAction`, `BlockAction`, `ItemAction`, `BiEntityAction` in JavaScript.
- **Custom Conditions**: Define `EntityCondition`, `BlockCondition`, `ItemCondition`, `BiEntityCondition`,
  `BiomeCondition`, `DamageCondition`, `FluidCondition` in JavaScript.
- **Custom Powers**: Create fully scripted Power types with JS callbacks (`grant`, `revoke`, `tick`, `active`,
  `inactive`).
- **Data-Driven**: All custom types can be referenced in JSON datapacks using the `origins_js:` namespace.
- **Parameters Support**: Pass custom parameters from JSON to your JS callbacks for dynamic behavior and high
  reusability.

## Dependencies

- [KubeJS](https://modrinth.com/mod/kubejs)
- [Origins (NeoForge)](https://modrinth.com/mod/origins-neoforge)

## Quick Start

### 1. Install all dependencies, then place your `.js` scripts in `kubejs/server_scripts/`.

### 2. Basic Usage

```javascript
// Get the holder for a player or entity
let holder = OriginsJS.getHolder(event.player);

// Manage origins & powers
holder.grantPower("origins:origin", "origins:fire_immunity");
holder.revokePower("origins:origin", "origins:climbing");
holder.setOrigin("origins:origin", "origins:human");

// Check state
if (holder.hasPower("origins:fire_immunity")) { /* ... */ }
if (holder.hasOrigin("origins:human")) { /* ... */ }
```

See [examples](https://docs.iafenvoy.com/docs/mod/origins/js/examples) for more examples including custom actions, conditions, and powers.

## Documentation

- [API Reference](https://docs.iafenvoy.com/docs/mod/origins/js/api-reference) — full type table, HolderWrapper API, and static shortcut methods
- [Examples](https://docs.iafenvoy.com/docs/mod/origins/js/examples) — code examples for origins/powers, custom actions, conditions, and powers
