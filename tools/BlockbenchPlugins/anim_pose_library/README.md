# Blockbench Animation Pose Library

A Blockbench 5.1.4 plugin that converts an existing animation frame into persistent Edit-mode bone pivots and rotations. It is designed to work with native Blockbench animations and GeckoLib Models & Animations.

## Features

- Define the current Edit-mode skeleton as the default pose.
- Sample any project animation at a chosen time (default: `0s`).
- Write the evaluated bone position and rotation into Edit-mode model data.
- Restore the saved default pose without discarding geometry edits made while posed.
- Store default-pose metadata inside the `.bbmodel` file under Blockbench's `unhandled_root_fields` namespace.
- Ignore animation scale channels.

## Installation

1. Open Blockbench 5.1.4.
2. Open **File > Plugins…**.
3. Choose **Load Plugin From File**.
4. Select `animation_pose_library.js`.

The commands are available under **Tools > Pose Library**.

## Usage

1. Open the project and switch to Edit mode.
2. Select **Tools > Pose Library > 定义默认姿势**.
3. Select **从动画应用姿势…**, choose an animation and sample time, then confirm.
4. Edit the model in the resulting pose.
5. Use **恢复默认姿势** to return the skeleton to the stored default while retaining element-local geometry changes.

Use **重新定义默认姿势** when the current Edit-mode skeleton should become the new baseline.

## Data compatibility

The actual applied pose is stored as ordinary Blockbench Group and element data. The saved default pose is namespaced at:

```text
unhandled_root_fields.blockbench_animation_pose_lib
```

Blockbench can therefore open the `.bbmodel` without this plugin. Because `unhandled_root_fields` is a built-in project property, the metadata is also retained when the file is opened and saved without Animation Pose Library. GeckoLib-format projects still require the GeckoLib Models & Animations plugin for full format support.

## Current scope

- Target version: Blockbench 5.1.4.
- Position and rotation only; scale is intentionally ignored.
- Bone matching primarily uses UUID, with a unique case-insensitive name fallback for stored default-pose data.
- Newly added bones that are absent from the saved default pose remain unchanged until the default pose is redefined.
