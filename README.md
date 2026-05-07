# Random Survival Events

Random Survival Events 是一个面向 Minecraft 1.21.1 / Fabric 的随机生存事件模组。它会在生存或冒险模式玩家游玩过程中周期性触发事件，包含补给、天气、怪物、方块、属性、配方混乱、重力异常、灾难和惩罚类事件。

## 版本

- Minecraft: `1.21.1`
- Fabric Loader: `0.19.2` 或更高
- Java: `21` 或更高
- Mod 版本: `1.0.0`
- Mod ID: `random-survival-events`

## 前置模组

必须安装：

- Fabric API `0.116.11+1.21.1` 或更高
- Cardinal Components API `6.1.3` 或更高
- Cloth Config API `15.0.140` 或更高
- Mod Menu `11.0.4` 或更高

`fabric.mod.json` 已同步声明这些前置依赖。Cloth Config API 和 Mod Menu 用于客户端配置界面；服务端或整合包发布时也应保持依赖列表一致，避免配置入口缺失导致加载问题。

## 玩法概览

- 默认每 `3600` tick 尝试触发一次随机事件。
- 大多数持续型事件默认持续 `3400` tick。
- 默认不允许多个持续型事件重叠，可通过配置开启。
- 事件按稀有度和类别加权选择，包含奖励、中性、方块、属性、配方、惩罚和灾难事件。
- 惩罚类事件只会作用于存活的生存或冒险模式玩家。
- 破坏模式默认关闭。开启后，部分惩罚事件才允许永久改变地形、物品或世界状态。

## 配置

首次运行后会生成：

```text
config/random-survival-events.json
```

常用配置项包括：

- `enableRandomEvents`: 是否启用随机事件。
- `eventIntervalTicks`: 随机事件触发间隔。
- `defaultEventDurationTicks`: 多数持续型事件的默认持续时间。
- `allowEventOverlap`: 是否允许持续型事件重叠。
- `enablePunishmentEvents`: 是否启用惩罚事件。
- `enableRewardEvents`: 是否启用奖励事件。
- `enableGravityChaos` / `enableGravityCrush`: 是否启用重力相关事件。
- `enableRecipeShuffleEvents`: 是否启用配方打乱事件。
- `enableBlockChaosEvents`: 是否启用方块异变事件。
- `destructiveMode`: 是否开启破坏模式。

安装 Mod Menu 和 Cloth Config API 后，可在客户端 Mod Menu 中打开配置界面。

## 管理员命令

所有 `/rse` 调试命令默认需要权限等级 `2`。

```mcfunction
/rse random
/rse trigger <event_id>
/rse trigger_rarity <common|uncommon|rare|epic|disaster>
/rse trigger_punishment
/rse preview <event_id>
/rse status
/rse recipechaos
/rse recipeshuffle
/rse effect <effect_id>
/rse config show
/rse config reload
/rse config set interval <ticks>
/rse config set duration <ticks>
/rse config set overlap <true|false>
/rse destructive on
/rse destructive off
```

## 构建

Windows:

```powershell
.\gradlew.bat build
```

Linux / macOS:

```bash
./gradlew build
```

构建产物位于：

```text
build/libs/
```

## 仓库

- Source: <https://github.com/ikunkk02-afk/random-survival-events>
- Issues: <https://github.com/ikunkk02-afk/random-survival-events/issues>

## 许可证

本项目使用 MIT License。详见 [LICENSE](LICENSE)。
