# Third-party notices

This project contains code and assets from other projects, and bundles a few
libraries inside the built jar. Those parts are **not** covered by this
project's MIT / CC BY-NC 4.0 grants. The upstream license applies to them, and
the upstream copyright notices are reproduced below as required.

中文说明见文末。

## Files in this repository

### Origins

- Upstream: <https://github.com/apace100/origins-fabric>
- License: MIT, Copyright (c) 2020 apace100
- Applies to:
  - `src/main/java/net/onixary/shapeShifterCurseFabric/integration/origins/**`
    (adapted from the Origins source)
  - `src/main/resources/assets/origins/**`
    (lang files and textures taken from the Origins mod)

These files are MIT licensed by their original author. They are **not** licensed
under CC BY-NC 4.0, even where they sit inside the asset trees listed in
`LICENSING.md`. Their copyright notice must be kept.

### 1-bit Pixel Icons

- Author: Nikoichu
- License: CC0 1.0 Universal
- License page: <https://creativecommons.org/publicdomain/zero/1.0/>
- Applies to: `3d_models/icon/1-bit_Pixel_Icons/**`

These icons retain the original author's CC0 1.0 dedication and are exempt from
this project's CC BY-NC 4.0 asset license. Attribution is appreciated by the
author but is not required.

Original author's statement:

> 1-bit Pixel Icons by Nikoichu is marked CC0 1.0
> All of these icons are licensed under CC0, which means you have absolutely no restrictions on how you can use them. I would still appreciate it if you gave me attribution for my work, but it's not strictly required.

### Avali sub-form models (concept from others)

- Upstream: the avali, an open species whose content is shared by its creator
  under CC BY-SA 2.0
- License: Creative Commons Attribution-ShareAlike 2.0 (CC BY-SA 2.0)
- License page: <https://creativecommons.org/licenses/by-sa/2.0/>
- Applies to:
  - `3d_models/player_form/bat_3_sub_avali/**`
  - `3d_models/anim_speed_scale/bat_3_sub_avali.json`
  - `src/main/resources/assets/shape-shifter-curse/geo/form/form_bat_3_sub_avali.geo.json`
  - `src/main/resources/assets/shape-shifter-curse/textures/form/form_bat_3_sub_avali/**`
  - `src/main/resources/assets/shape-shifter-curse/player_animation/avali_*.json`
  - `src/main/resources/assets/shape-shifter-curse/ssc_form_model/origins.origin.shape-shifter-curse.form_bat_3_sub_avali.json`

These are this project's own models rather than files taken from another project:
they are listed here because they depict the avali, a concept that comes from
someone else, and therefore have to follow the species' share-alike terms. They
are licensed under CC BY-SA 2.0, **not** under this project's CC BY-NC 4.0 asset
license even though they sit inside the asset trees listed in `LICENSING.md`
(which covers them in its section 5). The ShareAlike condition carries over to the
assets exported from them (listed above; they ship inside the built jar). Keep the
attribution to this mod and to the avali species when reusing them.

### tools/SignSystem

- Author: XuHaoNan
- License: All Rights Reserved (per the `LICENSE` header in each file)
- Applies to: `tools/SignSystem/**`

Not open source. Reuse or redistribution requires permission from the author.
See `tools/SignSystem/LICENSE.txt`.

## Libraries bundled inside the built jar

These are embedded into the mod jar with Loom's `include` (Jar-in-Jar), as
declared in `build.gradle`. Each is redistributed under its own license, and the
respective license text is available from the upstream repository.

| Library | Author | License | Upstream |
| --- | --- | --- | --- |
| Apoli | apace100 | MIT | <https://github.com/apace100/apoli> |
| PlayerAnimator (`player-animation-lib-fabric`) | KosmX | MIT, Copyright (c) 2022 KosmX | <https://github.com/KosmX/minecraftPlayerAnimator> |
| Reach Entity Attributes | JamiesWhiteShirt | MIT | <https://github.com/JamiesWhiteShirt/reach-entity-attributes> |

## Build-time only

| Component | License | Upstream |
| --- | --- | --- |
| MixinExtras (`libs/mixinextras-fabric-*.jar`) | MIT | <https://github.com/LlamaLad7/MixinExtras> |
| Gradle wrapper (`gradle/wrapper/gradle-wrapper.jar`) | Apache-2.0 | <https://github.com/gradle/gradle> |

## Declared dependencies, not redistributed

The remaining dependencies in `build.gradle` are resolved by Gradle at build time
and are not redistributed as source in this repository. They keep their own
licenses (Fabric API is Apache-2.0, GeckoLib, Satin is LGPL-3.0, Pehkui and
Cardinal Components API are MIT, and so on). Check each upstream project before
redistributing a build that changes which libraries are bundled.

## MIT license text (for the Origins-derived files)

```
MIT License

Copyright (c) 2020 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

# 中文版

本项目中包含来自其他项目的代码与资源，构建出的 jar 内也打包了若干第三方库。
这些部分**不受**本项目 MIT / CC BY-NC 4.0 授权的约束，适用其原始授权；按其
授权要求，此处附上原始版权声明。

## 仓库内采用其他授权的文件

- **Origins**（<https://github.com/apace100/origins-fabric>），MIT 授权，
  Copyright (c) 2020 apace100，适用于
  `src/main/java/net/onixary/shapeShifterCurseFabric/integration/origins/**`
  与 `src/main/resources/assets/origins/**`。
  这些文件按原作者授权保持 MIT，**不属于** CC BY-NC 4.0，即使它们位于
  `LICENSING.md` 列出的资源目录内；其版权声明必须保留。
- **1-bit Pixel Icons**，作者 Nikoichu，采用
  [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/)，适用于
  `3d_models/icon/1-bit_Pixel_Icons/**`。该图标包保留原作者的 CC0 1.0 授权，
  不适用本项目的 CC BY-NC 4.0 资源许可。作者欢迎署名，但不强制要求。
- **avali 子形态模型**（本项目自己的作品，并非取自其他项目的文件；列在此处是因为
  它们描绘的 avali 是源自他人的概念，须跟随其授权），其内容由原作者按
  [CC BY-SA 2.0](https://creativecommons.org/licenses/by-sa/2.0/) 共享，因此这些
  模型及其导出的资源采用 CC BY-SA 2.0，**不属于**本项目的 CC BY-NC 4.0 资源许可，
  即使它们位于 `LICENSING.md` 列出的资源目录内。适用文件：
  `3d_models/player_form/bat_3_sub_avali/**`、
  `3d_models/anim_speed_scale/bat_3_sub_avali.json`，以及
  `src/main/resources/assets/shape-shifter-curse/` 下由它们导出的
  `geo/form/form_bat_3_sub_avali.geo.json`、`textures/form/form_bat_3_sub_avali/**`、
  `player_animation/avali_*.json`、`ssc_form_model/origins.origin.shape-shifter-curse.form_bat_3_sub_avali.json`。
  相同方式共享的条款会延续到这些导出资源上（它们随构建出的 jar 一起分发），
  复用时请保留对本 mod 与 avali 物种的署名。
- **tools/SignSystem**，作者 XuHaoNan，按文件头声明为「保留所有权利」，
  不是开源代码，复用或再分发需取得作者许可。见
  `tools/SignSystem/LICENSE.txt`。

## 打包进 jar 的第三方库（Jar-in-Jar）

均通过 `build.gradle` 中的 `include` 嵌入，各自遵循其授权：

| 库 | 作者 | 授权 |
| --- | --- | --- |
| Apoli | apace100 | MIT |
| PlayerAnimator (`player-animation-lib-fabric`) | KosmX | MIT, Copyright (c) 2022 KosmX |
| Reach Entity Attributes | JamiesWhiteShirt | MIT |

## 仅构建期使用

MixinExtras（MIT）、Gradle wrapper（Apache-2.0）。

## 仅声明依赖、未再分发

`build.gradle` 中其余依赖由 Gradle 在构建时解析，本仓库未以源码形式再分发，
各自遵循其授权（Fabric API 为 Apache-2.0、Satin 为 LGPL-3.0、Pehkui 与
Cardinal Components API 为 MIT 等）。若改动打包的库，请先核对上游授权。
