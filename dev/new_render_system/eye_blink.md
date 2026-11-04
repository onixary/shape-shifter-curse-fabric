# 多组眼睛同步眨眼

在形态 JSON 的 `animation_system_config` 中，将 `eye_blink.eye` 写为眼骨名称数组：

```json
"eye_blink": {
  "eye": ["upperEyeRoot", "lowerEyeRoot", "sideEyeRoot"],
  "open_scale": 1.0,
  "close_scale": 0.01
}
```

每个名称对应一组眼睛的根骨。所有眼组共用眨眼节奏和开合比例，但各自在自己的枢轴处沿局部 Y 轴缩放；睡眠时全部闭眼。将各眼组枢轴放在对应眼睛的位置，避免眼睛缩向其他眼组。

- 旧写法 `"eye": "eyeRoot"` 仍然有效，无需迁移。
- 未配置 `eye` 或使用空数组时不执行眨眼；找不到某个骨骼时跳过该骨骼，其余眼组仍正常处理。
- 可以与 `neck_config` 同时使用。需要跟随动态头部的眼骨应放在该头骨（例如 `ik_head`）下。
- 不要同时列出某个眼骨及其祖先眼骨，否则子骨会继承祖先的缩放，再叠加自身缩放。
- 各眼组使用相同的 `open_scale` / `close_scale`，不单独随机眨眼。
