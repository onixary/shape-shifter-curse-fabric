# Shape Shifter Curse 1.9.2 → 1.10.0 Change Compilation (Player Perspective)

This directory contains the compiled development changes of Shape Shifter Curse (SSC) from **1.9.2** to **1.10.0**, intended for writing the wiki and the update changelog. Every conclusion is based on direct verification of git commits, code and resource files — not on paraphrasing commit titles.

> Chinese originals: `1.10.0-变更整理.md`, `附录A-Commit分类明细.md` and `README.md` in this directory.

## Deliverables

| File | Lines | Purpose |
|---|---|---|
| [1.10.0-changes-EN.md](1.10.0-changes-EN.md) | ~1070 | **Main document** (English): full player-visible change compilation in 10 chapters — the main source for the wiki |
| [Appendix-A-Commit-Categories.md](Appendix-A-Commit-Categories.md) | ~885 | 612 non-merge commits classified into 7 areas; Appendix B lists the 175 fix-type commits |
| [README-EN.md](README-EN.md) | this file | Method, version-scope determination, raw data, reproduction steps, open questions |

Chinese equivalents: `1.10.0-变更整理.md`, `附录A-Commit分类明细.md` and `README.md` (same directory).

## Structure of the main document

1. **1. Version Scope and Method** — how the version boundary was determined, statistics conventions, classification rules
2. **2. Overview** — one page that captures the player-visible big picture of 1.10.0 vs. 1.9.2
3. **3. Form System and Forms** — new form system, sub-forms (Avali / Marbled Polecat), Cursed Moon, instinct, initial form, fallback, phase names, commands
4. **4. Perk Skill System** — 9 trees / 68 nodes, Form Tuner Beacon, Altar, costs and recipes
5. **5. Items, Blocks and Cross-Mod Content** — 27 new items, Altar/Form Tuner Beacon blocks, recipes, food/effects/entities, 11 mod integrations, datapacks
6. **6. UI, Advancements, Localization and Keybinds** — Altar GUI, Mirror Reflection, codex, advancement conditions, keybinds, four-language localization, magic-system WIP
7. **7. Visuals: Rendering / Models / Animation** — GeckoLib migration, models/animation, blinking, long-neck IK, multiplayer/cape/tail rendering, visual fixes
8. **8. Balance Changes and Player-Visible Fixes** — number tables plus symptom → after-fix lists
9. **9. Config Options, Gamerules and Technical Migration** — config additions/removals, gamerules, network/event API, Origins namespace migration, patron verification
10. **10. Wiki Writing Guide** — site structure, page skeleton, the 14-item fact-check list, data quick-reference

Every conclusion carries a **short commit hash + evidence path** and an impact tag (all players / players of a specific form / client/server / modpack & datapack authors).

## Version-scope determination (key)

- The repository has no git tags and the GitHub releases API returns nothing, so the boundary was cross-checked using local build artifacts and version-bump commits:
  - The 1.9.2 release jar was built at **2026-06-14 12:27**; the last master commit before `89901e02` (2026-06-14 17:21, "since 1.9.2 is already released, let's remove the powers bundled with origins") is **`a96f557d`** (2026-06-14 10:52, Merge PR #445).
  - **1.10.0 range = `a96f557d..HEAD`** (HEAD = `b6a03f01`, 2026-10-26): 776 commits (612 non-merge + 164 merge), 1780 files, +96,721 / −18,186.
- Most 1.10.0 development happened on the new-form-system branch `form_system_refractor` (started 2026-06-05, merged via PR #457 around 2026-07-23) and in the September–October Perk/Altar push.

## Method

1. **Lead**: first built the global baseline (diff statistics, registry/language/resource inventories), then split the work into 6 area reports, and finally unified the facts into the main document.
2. **6 areas**: form system & sub-forms; Perk/Form Tuner Beacon/Altar; rendering & animation; items & cross-mod; UI/advancements/localization/keybinds; balance/fixes/config/technical migration. Each area produced one report (`build/tmp/ssc1100/reports/`) and one chapter (`build/tmp/ssc1100/sections/`).
3. **Cross-checking** corrected several conclusions that commit titles alone would have gotten wrong, for example:
   - `248cd770` is not a runtime visual change (it only updated plugin metadata of bbmodels under `3d_models/`); the actual renderer migration is `66837b32` (AzureLib → GeckoLib 4.8.4);
   - the "Form Customization Menu V2" already existed in 1.9.2 (1,317 lines); 1.10.0 only contains three fixes/tweaks, so it is not a new feature;
   - "localization adds en_gb" is inaccurate: en_gb existed in 1.9.2 and was completed/British-ised in this version;
   - "16 advancement criteria merged / dynamic patch" is false: both versions have 45 advancements (17 hidden); only 5 transform-advancement condition fields changed.
   - See "Fact-check list" (14 items) in Chapter 10 of the main document.

## Raw data and reproduction

All scripts, baseline data, reports and chapters are kept in `build/tmp/ssc1100/` (not tracked by git):

```
build/tmp/ssc1100/
├── commits.tsv / commits_lite.tsv      # 612 non-merge commit list
├── extract_baseline.py / extract_baseline2.py
├── global_stats.py                     # global statistics (authors/months/packages/resources)
├── classify_commits.py                 # area classification of commits
├── baseline/                           # baseline data (see below)
│   ├── commit_files.tsv                # files touched by each commit
│   ├── commit_classified.tsv           # hash/date/title/area/fix·balance flags/files
│   ├── commit_appendix.md / commit_appendix_fix.md
│   ├── global_stats.md
│   ├── reg_*.md, forms_diff.md, perks_full.md, perk_trees_full.md
│   ├── lang_*.json, lang_diff.md
│   ├── java_added.txt / java_removed.txt
│   └── resources_added.txt / resources_removed.txt, resources_by_type.md
├── reports/                            # 6 area reports (raw, more detail)
├── sections/                           # Chinese main-document chapters (assembled 00,03~10)
└── i18n/                               # English translation chunks (src/ + en/)
```

Regenerate the Chinese main document:

```bash
cd build/tmp/ssc1100/sections
for f in 00-overview.md 03-form.md 04-perk.md 05-items.md 06-ui-l10n.md 07-render.md 08-balance-fixes.md 09-config-tech.md 10-wiki-guide.md; do
  [ "$f" = "00-overview.md" ] || printf '\n---\n\n'
  cat "$f"; printf '\n'
done > ../../../docs/1.10.0_change_log/1.10.0-变更整理.md
```

The English main document was produced by translating chunks (`build/tmp/ssc1100/i18n/src/01..05`) into `build/tmp/ssc1100/i18n/en/` and concatenating them in the same order.

## Open questions / limitations already flagged in the documents

The following could not be 100% determined from the repository; before writing the wiki, verify against the final release jar or ask the author:

1. **Whether the full model remaster ships with 1.10.0**: the post-September models only exist in `3d_models/` (Blockbench sources); the runtime `assets/.../geo/form/` still holds the old models. If the release jar exports the new models, the "visual changes" in Chapter 7 should be expanded accordingly.
2. **Recommended GeckoLib version**: `fabric.mod.json` declares `>=4.0.0` and breaks on `>=5.0.0`; testing was done with 4.8.4 — the download page should say "latest 4.x".
3. **Altar/Form Tuner Beacon block drops (fixed after delivery)**: the working tree now has loot tables (drop themselves, blast-resistant) and a `mineable/pickaxe` tag, with hardness lowered from 4.0 to 3.0 and `requiresTool()` added (any pickaxe tier works); confirm these changes ship in the release jar — the main document already describes the fixed state.
4. **Magic system**: `MagicUtils.shouldShowMagicUI` is hard-coded to true (development comment), so clients may see the three-slot wheel HUD; the system is not wired into gameplay and is documented as "unreleased, may leak a HUD".
5. **Purpose of the `SU_*` mixins** (sponsor/special build related) is not fully confirmed; not expanded in the documents.
6. **10 commented-out perks** (e.g. Bat Air Blast/Night Veil, Axolotl water collecting, Ocelot long pounce, Snow Fox Cold Whirlwind) — whether temporarily disabled or removed needs a future version to decide.
7. Test leftovers inside resources (`_test_perk01.json`, `test_power.json`, `form_long_cat_test.geo.json`, `form_axolotl_3.geo.json.backup`) are development artifacts and should not be documented in the wiki.
8. Some `form_spider_0` / `form_spider_2` checks are marked "to be confirmed"; whether the old form-select screen `custom_ui/NormalFormSelectScreen.java` (patron-item path) is publicly reachable is also unconfirmed.

## Usage tips

- **Writing the wiki**: start with Chapter 2 for the big picture, then take material chapter by chapter from 3–9; Chapter 10 provides the site structure, page skeleton and the "14 easy-to-get-wrong facts" as a writing checklist.
- **Writing the changelog**: the Chinese and English changelog drafts are treated as private files and are not included in this directory — use Chapters 2 and 8 of the main document plus Appendix A as source material and keep the English summary for the Modrinth/CurseForge description; before publishing, re-check items 1 and 4 of the open-questions list above (item 3 was fixed after delivery; only confirm that the change ships).
- **Tracing a change**: use the area tables in Appendix A plus the short hashes in the main document; `build/tmp/ssc1100/baseline/commit_files.tsv` gives the file list of each commit for deeper digging.
