# Quick Shulker

[English](README.md) | **中文**

用按键快速打开手持的潜影盒！

这个项目将 [kyrptonaught](https://github.com/kyrptonaught) 的 [QuickShulker](https://github.com/kyrptonaught/quickshulker.git) 模组更新到更高的我的世界版本并且修复了一些东西。

---

## Latest Release

### Fabric：

点击下面的链接下载。

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

> 如果你需要 1.20.2~1.20.6，点击[这里](https://moranpcy.lanzouq.com/b004io7t1a)，密码是`1ipd`，它们已经停止维护了，所以存在许多问题。

### Neoforge:

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

## 功能

### 快速打开物品

你可以使用快捷键（默认是<kbd>k</kbd>）或者鼠标右键快速打开玩家手里或者物品栏内物品的界面。

| 支持的物品 |
|  :-----:  |
|工作台|
|切石机|
|潜影盒|
|末影箱|
|铁砧  |
|收纳袋|

> 对于 Fabric 你可以通过在服务端禁用收纳袋来允许未装模组的客户端玩家进入。

### 容器的快捷操作

* 你可以用鼠标拖动容器右键物品来存入容器，也可以用物品右键容器。
* 你可以用鼠标拖动容器右键物品栏内的空槽位来取出容器内的物品。
* 你可以用鼠标拖动并长按鼠标右键来批量存入或者取出容器内的物品。

### 配置菜单

模组提供了一个配置菜单以便你更方便地启用或者禁用某些功能，你可以使用[Mod Menu](https://modrinth.com/mod/modmenu)来打开它，但不是必须的，你可以用一个可配置的按键来打开它，默认是小键盘的<kbd>+</kbd>。

### API

原作者提供了一个API可以让你的模组里的物品也可以做到[快速打开物品](#快速打开物品)和[容器的快捷操作](#容器的快捷操作)。

你需要实现 `RegisterQuickShulker` ，然后在 `registerProviders()` 中注册你的模组，以`26.2`为例：

* 为你的模组注册[快速打开物品](#快速打开物品)。

    <details>
    <summary>点击展开</summary>

    ```java
    import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;

    public class YourClass implements RegisterQuickShulker {
        @Override
        public void registerProviders() {
            if (...) // 你可以在这里设置启用或者禁用的条件
                new QuickOpenableRegistry.Builder()
                    .setItem(YourBlockOrItem.class) // 必需的
                    .ignoreSingleStackCheck(true)  // 可选的。设置物品是否能在堆叠时打开，就像工作台和铁砧，默认是false
                    .setOpenAction((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, player) -> 
                            new YourItemMenu(...), YourMenuTitle))) // 必需的
                    .register();
        }
    }
    ```

    </details>

* 为你的模组注册[容器的快捷操作](#容器的快捷操作)。
   
    <details>
    <summary>点击展开</summary>

    ``` java
    import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;

    public class YourClass implements RegisterQuickShulker {
        @Override
        public void registerProviders() {
            new QuickOpenableRegistry.Builder()
                .setItem(YourBlockOrItem.class) // 必需的
                .supportsBundleing(true) // 必需的。默认是false
                .getBundleInv((player, stack) -> new YourItemContainer()) // 必需的
                .register();
        }
    }

   ```

    </details>