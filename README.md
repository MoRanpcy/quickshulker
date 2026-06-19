# Quick Shulker

**English** | [中文](README_CN.md)

Quickly open a held shulker box with the press of a key!

This project updates [kyrptonaught](https://github.com/kyrptonaught)'s [QuickShulker](https://github.com/kyrptonaught/quickshulker.git) mod to higher Minecraft versions，and fixes something.

---

## Latest Release

### Fabric:

Click the links below to download.

<details>
<summary>1.21.x</summary>

* [3.0.0-1.21.1](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.0-1.21.1/quickshulker-3.0.0-1.21.1.jar)
* [3.0.1-1.21.2](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.2/quickshulker-3.0.1-1.21.2.jar)
* [3.0.1-1.21.3](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.3/quickshulker-3.0.1-1.21.3.jar)
* [3.0.1-1.21.4](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.4/quickshulker-3.0.1-1.21.4.jar)
* [3.0.1-1.21.5](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.5/quickshulker-3.0.1-1.21.5.jar)
* [3.0.1-1.21.6](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.6/quickshulker-3.0.1-1.21.6.jar)
* [3.0.1-1.21.7](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.7/quickshulker-3.0.1-1.21.7.jar)
* [3.0.1-1.21.8](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.8/quickshulker-3.0.1-1.21.8.jar)
* [3.0.1-1.21.9](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.9/quickshulker-3.0.1-1.21.9.jar)
* [3.0.1-1.21.10](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.10/quickshulker-3.0.1-1.21.10.jar)
* [3.0.1-1.21.11](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-1.21.11/quickshulker-3.0.1-1.21.11.jar)

</details>

<details>
    <summary>26.x</summary>

* [3.0.1-26.1](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.1-26.1/quickshulker-3.0.1-26.1.jar)
* [3.0.2-26.2](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.2-26.2/quickshulker-3.0.2-26.2.jar)

</details>

> If you need 1.20.2 ~ 1.20.6, click [here](https://moranpcy.lanzouq.com/b004io7t1a) (password: `1ipd`). They are no longer maintained and have several issues.

### NeoForge:

<details>
<summary>26.x</summary>

* [3.0.2-26.1](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.2-26.1-neo/quickshulker-neo-3.0.2-26.1.jar)
* [3.0.2-26.2](https://github.com/MoRanpcy/quickshulker/releases/download/3.0.2-26.2-neo/quickshulker-neo-3.0.2-26.2.jar)

</details>

## Beta

### Fabric:

<details>
<summary>1.21.6-1.21.8</summary>

* [1.21.6-1.21.8-beta.1](https://github.com/MoRanpcy/quickshulker/releases/download/1.21.6-1.21.8-beta.1/quickshulker-1.21.6-1.21.8-beta.1.jar)

</details>

---

## Features

### Quick Open Item

Use a hotkey (default: <kbd>k</kbd>) or right-click to quickly open the screen of an item held in your hand or stored in your inventory.

| Supported Items  |
| :--------------: |
| Crafting Table   |
| Stonecutter      |
| Shulker Box      |
| Ender Chest      |
| Anvil            |
| Bundle           |

> On Fabric, you can disable the bundle on the server side to allow clients without mod to join.

### Quick Container Actions

* Drag a container item and right‑click another item to store that item into the container; you can also right‑click the container with an item.
* Drag a container item and right‑click an empty slot in your inventory to extract items from the container.
* Drag a container item and hold the right mouse button to batch‑store or batch‑extract items.

### Config Menu

A config menu is provided so you can easily enable or disable certain features. You can open it via [Mod Menu](https://modrinth.com/mod/modmenu), but it is not required – you can also use a configurable hotkey (default: numpad<kbd>+</kbd>).

### API

The original author provides an API that allows items from your own mod to also support [Quick Open Item](#quick-open-item) and [Quick Container Actions](#quick-container-actions).

You need to implement `RegisterQuickShulker` and register your mod in `registerProviders()`. Here is an example for version `26.2`:

* Register [Quick Open Item](#quick-open-item) for your mod.

    <details>
    <summary>Click to expand</summary>

    ```java
    import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;

    public class YourClass implements RegisterQuickShulker {
        @Override
        public void registerProviders() {
            if (...) // You can add conditions to enable/disable here
                new QuickOpenableRegistry.Builder()
                    .setItem(YourBlockOrItem.class) // Required
                    .ignoreSingleStackCheck(true)  // Optional. Set whether the item can be opened even when stacked (like Crafting Table or Anvil). Default is false.
                    .setOpenAction((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, player) -> 
                            new YourItemMenu(...), YourMenuTitle))) // Required
                    .register();
        }
    }
    ```

    </details>

* Register [Quick Container Actions](#quick-container-actions) for your mod.

    <details>
    <summary>Click to expand</summary>

    ```java
    import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;

    public class YourClass implements RegisterQuickShulker {
        @Override
        public void registerProviders() {
            new QuickOpenableRegistry.Builder()
                .setItem(YourBlockOrItem.class) // Required
                .supportsBundleing(true) // Required. Default is false.
                .getBundleInv((player, stack) -> new YourItemContainer()) // Required
                .register();
        }
    }
    ```

    </details>