# Licensing

This repository is **dual-licensed**: source code under MIT, media assets under
Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0).
中文说明见下方 [中文版](#中文版)。

## Summary

| Content | License | License file |
| --- | --- | --- |
| Source code | MIT | [`LICENSE.txt`](LICENSE.txt) |
| Media assets (3D models, textures, animations, sounds, fonts, translations) | CC BY-NC 4.0 | [`LICENSE-CC-BY-NC-4.0.txt`](LICENSE-CC-BY-NC-4.0.txt) |
| Third-party code and assets we reuse | Their upstream license | [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md) |

Note that CC BY-NC 4.0 is *not* an open-source license, because it forbids
commercial use. Only the media assets are covered by it; the code stays
permissive.

## 1. Code - MIT

Everything below is licensed under the MIT License in [`LICENSE.txt`](LICENSE.txt).

| Path | Content |
| --- | --- |
| `src/main/java/**` | Java source |
| `src/main/resources/**`, except `assets/**` | datapack data, `fabric.mod.json`, mixin and access-widener configs |
| `src/main/resources/assets/shape-shifter-curse/shaders/**` | GLSL shader source |
| `build.gradle`, `settings.gradle`, `gradle.properties`, `gradle/**`, `gradlew*` | build scripts |
| `.github/**` | CI workflows |
| `tools/**`, except `tools/SignSystem/**` | helper scripts (see [section 4](#4-third-party-content) for `tools/SignSystem`) |
| `custom_form_pack_example/**/*.json`, `custom_form_pack_example/**/*.mcmeta` | example pack logic |
| `PatronServer/*.py` | patron data server |
| `dev/**` | design notes and scratch code |
| `*.md` | documentation |

Anything not listed in [section 2](#2-media-assets---cc-by-nc-40) or
[section 4](#4-third-party-content) is code and falls under MIT.

## 2. Media assets - CC BY-NC 4.0

Everything below is licensed under CC BY-NC 4.0 in
[`LICENSE-CC-BY-NC-4.0.txt`](LICENSE-CC-BY-NC-4.0.txt).

| Path | Content |
| --- | --- |
| `3d_models/**` | Blockbench project files, colormasks, reference art |
| `src/main/resources/assets/shape-shifter-curse/**`, except `shaders/**` | textures, GeckoLib geo models, animation JSON, form models, GUI, sounds, `lang/**` and `rich_lang/**` translations |
| `PatronServer/PackRes/**` | patron form packs |
| `PatronServer/WebRoot/*.zip` | built patron form packs |
| `custom_form_pack_example/**`, except `*.json` and `*.mcmeta` | example pack textures and models |

You may share and adapt these assets for non-commercial purposes, provided you
give appropriate credit, link to the license, and indicate if changes were made.
You may not use them for commercial purposes.

Recommended attribution text:

> Shape Shifter Curse assets by onixary and contributors,
> licensed under CC BY-NC 4.0. https://github.com/onixary/shape-shifter-curse-fabric

## 3. What this means for the built mod

The built `jar` and `sources-jar` contain both code and assets, so they are a
mixed-license distribution: the code portions are MIT, the embedded assets are
CC BY-NC 4.0. Redistributing the unmodified jar, including bundling it in a
modpack or a server pack, is allowed as long as the use is non-commercial and the
license files shipped inside the jar are kept.

Extracting assets from the jar and using them commercially is not allowed.

## 4. Third-party content

Some files in this repository come from other projects, or are contributed by
other people under a different license. They are **not** covered by the grants
above; the upstream license applies instead. See
[`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md).

Notably:

- `src/main/java/net/onixary/shapeShifterCurseFabric/integration/origins/**` and
  `src/main/resources/assets/origins/**` are derived from the Origins mod by
  apace100, which is MIT licensed. They stay MIT.
- `tools/SignSystem/**` carries a per-file header `LICENSE: All Rights Reserved 
  (ShapeShifterCurse 开发组 - XuHaoNan & Onixary)`. Those files are all rights reserved and are **not** MIT licensed,
  despite living in this repository. Redistributing or reusing them requires
  permission from XuHaoNan or Onixary.

Dependencies declared in `build.gradle` (Fabric API, GeckoLib, Satin, Pehkui,
Cardinal Components API, Cloth Config, PlayerAnimator, and so on) are not
redistributed as source here and keep their own licenses.

## 5. Contributions

Contributions are accepted under the same split, unless stated otherwise in the
pull request:

- code contributions (Java, build scripts, tooling) - MIT
- asset contributions (models, textures, animations, sounds, fonts,
  translations) - CC BY-NC 4.0

By submitting a pull request to a given path you agree to license your
contribution under the license that applies to that path. If you are
contributing assets you did not create yourself, you must have the right to
license them this way.

If you would like to reuse the assets under different terms, ask first - contact
information is in [`fabric.mod.json`](src/main/resources/fabric.mod.json).

---

# 中文版

本仓库采用**双授权**：源代码使用 MIT，媒体资源使用「知识共享 署名-非商业性使用
4.0 国际」（CC BY-NC 4.0）。

## 总览

| 内容 | 授权 | 协议文件 |
| --- | --- | --- |
| 源代码 | MIT | [`LICENSE.txt`](LICENSE.txt) |
| 媒体资源（3D 模型、贴图、动画、音效、字体、翻译文本） | CC BY-NC 4.0 | [`LICENSE-CC-BY-NC-4.0.txt`](LICENSE-CC-BY-NC-4.0.txt) |
| 第三方代码与资源 | 遵循其原始授权 | [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md) |

注意：CC BY-NC 4.0 禁止商业使用，因此它**不属于**开源许可证。只有媒体资源
受其约束，代码部分仍保持宽松授权。

## 1. 代码 — MIT

以下内容采用 [`LICENSE.txt`](LICENSE.txt) 中的 MIT 许可证。

| 路径 | 内容 |
| --- | --- |
| `src/main/java/**` | Java 源码 |
| `src/main/resources/**`（`assets/**` 除外） | 数据包数据、`fabric.mod.json`、Mixin 与 Access Widener 配置 |
| `src/main/resources/assets/shape-shifter-curse/shaders/**` | GLSL 着色器源码 |
| `build.gradle`、`settings.gradle`、`gradle.properties`、`gradle/**`、`gradlew*` | 构建脚本 |
| `.github/**` | CI 工作流 |
| `tools/**`（`tools/SignSystem/**` 除外） | 辅助脚本（`tools/SignSystem` 见第 4 节） |
| `custom_form_pack_example/**/*.json`、`**/*.mcmeta` | 示例包逻辑 |
| `PatronServer/*.py` | 赞助者数据服务端 |
| `dev/**` | 设计笔记与临时代码 |
| `*.md` | 文档 |

凡未在第 2 节（媒体资源）或第 4 节（第三方内容）中列出的文件，均属于代码，
适用 MIT 授权。

## 2. 媒体资源 — CC BY-NC 4.0

以下内容采用
[`LICENSE-CC-BY-NC-4.0.txt`](LICENSE-CC-BY-NC-4.0.txt) 中的 CC BY-NC 4.0 许可证。

| 路径 | 内容 |
| --- | --- |
| `3d_models/**` | Blockbench 工程文件、色掩膜、参考图 |
| `src/main/resources/assets/shape-shifter-curse/**`（`shaders/**` 除外） | 贴图、GeckoLib 模型、动画 JSON、形态模型、GUI、音效、`lang/**` 与 `rich_lang/**` 翻译 |
| `PatronServer/PackRes/**` | 赞助者形态包 |
| `PatronServer/WebRoot/*.zip` | 构建好的赞助者形态包 |
| `custom_form_pack_example/**`（`*.json`、`*.mcmeta` 除外） | 示例包贴图与模型 |

你可以在**非商业**目的下共享和演绎这些资源，但必须署名、附带许可证链接，并
注明是否做过修改；不得用于商业用途。

建议的署名格式：

> Shape Shifter Curse 资源，作者 onixary 及贡献者，采用 CC BY-NC 4.0 授权。
> https://github.com/onixary/shape-shifter-curse-fabric

## 3. 构建产物

构建出的 `jar` 与 `sources-jar` 同时包含代码与资源，属于混合授权分发物：
其中代码部分为 MIT，随包资源为 CC BY-NC 4.0。只要用途非商业、并保留 jar 内
附带的协议文件，就可以原样再分发（包含放进整合包、服务端包）。

不允许把资源从 jar 中提取出来用于商业用途。

## 4. 第三方内容

仓库中的一部分文件来自其他项目，或由他人在不同授权下贡献。它们**不受**上述
授权的约束，而适用其原始授权。详见
[`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md)。

主要例外：

- `src/main/java/net/onixary/shapeShifterCurseFabric/integration/origins/**` 与
  `src/main/resources/assets/origins/**` 派生自 apace100 的 Origins 模组，
  该项目为 MIT 授权，因此这些文件保持 MIT。
- `tools/SignSystem/**` 的文件头写有 `LICENSE: All Rights Reserved (ShapeShifterCurse 开发组 - XuHaoNan & Onixary)`，
  属于「保留所有权利」，**不是** MIT 授权。若要再分发或复用其中的代码，
  需要取得 XuHaoNan 或 Onixary 的许可。

`build.gradle` 中声明的依赖（Fabric API、GeckoLib、Satin、Pehkui、Cardinal
Components API、Cloth Config、PlayerAnimator 等）并未以源码形式在本仓库中再
分发，各自遵循其授权。

## 5. 贡献

除在 PR 中另有说明外，贡献按同样的划分方式接受：

- 代码贡献（Java、构建脚本、工具脚本）— MIT
- 资源贡献（模型、贴图、动画、音效、字体、翻译）— CC BY-NC 4.0

向某个路径提交 PR，即表示你同意按该路径适用的许可证授权你的贡献。若你提交的
资源并非你本人创作，你必须有权以该方式对其进行授权。

如果你希望以其他条款复用这些资源，请先联系：联系方式见
[`fabric.mod.json`](src/main/resources/fabric.mod.json)。
