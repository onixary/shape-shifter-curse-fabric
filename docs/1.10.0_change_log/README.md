# 1.9.2 → 1.10.0 变更整理交付物（玩家视角）

本目录是 Shape Shifter Curse（SSC）从 **1.9.2** 到 **1.10.0** 的开发变更整理成果，供撰写 Wiki 与更新日志使用。所有结论都基于 git 提交、代码与资源文件的直接核对，而不是提交标题的复述。

## 交付物

| 文件 | 行数 | 用途 |
|---|---|---|
| [1.10.0-变更整理.md](1.10.0-变更整理.md) | 1069 | **主文档**：按玩家可感知内容分类的完整变更整理（10 章），写 Wiki 的主要素材 |
| [附录A-Commit分类明细.md](附录A-Commit分类明细.md) | 885 | 612 个非 merge commit 按 7 个领域分类的明细表；附录 B 为 175 条修复类提交 |
| README.md | 本文件 | 方法、数据源、复现方式、待确认事项 |

**英文版**（同一目录）：[1.10.0-changes-EN.md](1.10.0-changes-EN.md)（主文档）、[Appendix-A-Commit-Categories.md](Appendix-A-Commit-Categories.md)（附录 A/B，commit 标题保留原始中文）、[README-EN.md](README-EN.md)（本文件的英文版）。

## 主文档结构

1. **一、版本范围与整理方法** — 版本边界判定、统计口径、分类规则
2. **二、总览** — 一页纸看懂 1.10.0 相比 1.9.2 的玩家视角大图
3. **三、变形系统与形态** — 新变形系统、子形态（羽族/虎鼬）、诅咒之月、本能、初始形态、回退、阶段命名、命令
4. **四、Perk 技能体系** — 9 棵树 68 节点、调谐信标、幻形祭坛、成本与配方
5. **五、物品、方块与跨模组内容** — 27 件新物品、祭坛/信标方块、配方、食物/效果/实体、11 个模组兼容、数据包
6. **六、界面、成就、本地化与按键** — 祭坛 GUI、镜中倒影、图鉴、成就判定、按键、四语言本地化、魔法系统 WIP
7. **七、视觉与表现** — GeckoLib 迁移、模型动画、眨眼、长脖 IK、多人/披风/尾巴渲染、视觉修复
8. **八、平衡调整与玩家可感修复** — 数值表 + 症状→修复后的清单
9. **九、配置项、游戏规则与技术迁移** — 配置项增删、游戏规则、网络/事件 API、Origins 命名空间迁移、赞助者验证
10. **十、Wiki 编写建议** — 站点结构、页面骨架、事实纠错清单、数据速查表

每条结论都带**短 commit hash + 证据路径**，并标注影响面（所有玩家 / 某形态 / 客户端/服务端 / 整合包与数据包作者）。

## 版本范围判定（关键）

- 仓库没有 git tag，GitHub releases 接口返回空，因此用本地构建产物时间 + 版本号提交交叉确认：
  - 1.9.2 发布包构建于 **2026-06-14 12:27**；`89901e02`（2026-06-14 17:21，"既然1.9.2已经发布了 还是把origins自带的power移除吧"）之前最后一个 master 提交是 **`a96f557d`**（2026-06-14 10:52，Merge PR #445）。
  - **1.10.0 范围 = `a96f557d..HEAD`**（HEAD = `b6a03f01`，2026-10-26）：776 commits（612 非 merge + 164 merge），1780 files，+96,721 / −18,186。
- 1.10.0 的主要开发发生在新变形系统分支 `form_system_refractor`（2026-06-05 起，PR #457 于 2026-07-23 前后合入），以及 9~10 月的 Perk/祭坛开发。

## 方法

1. **Lead**：先做全局基线（diff 统计、注册表/语言/资源清单），再按领域拆分 6 份专题报告，最后统一事实口径、合并成主文档。
2. **6 个专题**：变形系统与子形态、Perk/调谐/祭坛、渲染与动画、物品与跨模组、界面/成就/本地化/按键、平衡/修复/配置与技术迁移。每个专题一份报告（`build/tmp/ssc1100/reports/`）和一份章节（`build/tmp/ssc1100/sections/`）。
3. **交叉核对**：修正了多处仅看提交标题会写错的结论，例如：
   - `248cd770` 不是运行时外观变化（只是 3d_models 下 bbmodel 的插件元数据），真正的渲染库迁移是 `66837b32`（AzureLib → GeckoLib 4.8.4）；
   - 「形态调色菜单 V2」在 1.9.2 已有完整实现，1.10.0 只有 3 处修复/关闭，并非新功能；
   - 「本地化新增 en_gb」不准确，en_gb 在 1.9.2 已存在，本版是补全并英式化；
   - 「成就 16 Criterion 合并/动态 patch」不成立：两版都是 45 个成就、17 隐藏，本版只改了 5 个变形成就的条件字段。
   - 详见主文档第十章「事实纠错清单」（14 条）。

## 原始数据与复现

所有脚本、基线数据、报告与章节都保留在 `build/tmp/ssc1100/`（未加入版本库）：

```
build/tmp/ssc1100/
├── commits.tsv / commits_lite.tsv      # 612 条非 merge 提交清单
├── extract_baseline.py / extract_baseline2.py
├── global_stats.py                     # 全局统计（作者/月份/包/资源）
├── classify_commits.py                 # commit 领域分类
├── baseline/                           # 基线数据（见下）
│   ├── commit_files.tsv                # 每个 commit 的改动文件（已按领域可用）
│   ├── commit_classified.tsv           # hash/日期/标题/领域/修复·平衡标记/文件
│   ├── commit_appendix.md / commit_appendix_fix.md
│   ├── global_stats.md
│   ├── reg_*.md, forms_diff.md, perks_full.md, perk_trees_full.md
│   ├── lang_*.json, lang_diff.md
│   ├── java_added.txt / java_removed.txt
│   └── resources_added.txt / resources_removed.txt, resources_by_type.md
├── reports/                            # 6 份专题报告（原始、含更多细节）
└── sections/                           # 主文档各章（main 由 00,03~10 按顺序拼接）
```

重新生成主文档：

```bash
cd build/tmp/ssc1100/sections
for f in 00-overview.md 03-form.md 04-perk.md 05-items.md 06-ui-l10n.md 07-render.md 08-balance-fixes.md 09-config-tech.md 10-wiki-guide.md; do
  [ "$f" = "00-overview.md" ] || printf '\n---\n\n'
  cat "$f"; printf '\n'
done > ../../../docs/1.10.0-变更整理.md
```

（章节文件是主文档的唯一来源；改动请先改 `sections/`，再重新拼接。）

## 待确认 / 文档中已标注的限制

以下事项在仓库内无法 100% 判定，写 Wiki 前建议对照最终发布版 jar 或询问作者：

1. **全形态模型重做是否随 1.10.0 发布**：9 月后的新模型只存在于 `3d_models/`（Blockbench 源文件），运行时 `assets/.../geo/form/` 仍是旧模型。若发布包导出了新模型，第七章的「外观变化」需相应扩大。
2. **GeckoLib 推荐版本**：`fabric.mod.json` 写 `>=4.0.0`、`breaks >=5.0.0`，实际测试按 4.8.4 进行；发布页建议注明「4.x 最新版」。
3. **祭坛/信标方块掉落（交付后已补齐）**：工作区新增 loot table（掉落本体、抗爆）与 `mineable/pickaxe` 标签，方块硬度 4.0→3.0 并 `requiresTool()`（任意等级镐可挖）；需确认改动进入发布 jar，主文档已按补齐后状态更新。
4. **魔法系统**：`MagicUtils.shouldShowMagicUI` 恒为 true（开发中注释），客户端可能显示三槽轮盘 HUD；系统未接入玩法，文档按「未发布、可能有 HUD 泄漏」记录。
5. **`SU_*` 系列 mixin 用途**（sponsor/special build 相关）未完全确认，文档未展开。
6. **10 个被注释禁用的 Perk**（如蝙蝠气流爆破/夜幕、美西螈汲水、豹猫长扑、雪狐寒旋风等）是临时关闭还是移除，需看后续版本。
7. 资源内遗留测试文件（`_test_perk01.json`、`test_power.json`、`form_long_cat_test.geo.json`、`form_axolotl_3.geo.json.backup`）属开发遗留，不应写进 Wiki。
8. `form_spider_0` / `form_spider_2` 部分判定标记了「待确认」；`custom_ui/NormalFormSelectScreen.java` 旧形态选择界面（赞助者道具路径）是否对外使用待确认。

## 使用建议

- **写 Wiki**：先读主文档第二章了解全貌，再按第三章~第九章逐页取素材；第十章给出了站点结构、页面骨架和「容易被写错的 14 条」，可直接当写作清单。
- **写更新日志**：中文与英文更新日志草稿按私用文件处理、不纳入本目录（撰写时以主文档第二、八章与附录 A 为素材，保留 English Summary 作为 Modrinth/CurseForge 英文说明）；发布前按「待确认」清单核对第 1、4 条（第 3 条已在交付后修复，仅需确认改动进入发布 jar）。
- **查改动依据**：用附录 A 的领域分类表 + 主文档里的短 hash 定位提交；`build/tmp/ssc1100/baseline/commit_files.tsv` 可进一步查每个提交改动的文件清单。
