package net.onixary.shapeShifterCurseFabric.player_animation.v2;

public enum AnimationControllerCellResult {
    MATCH,  // 直接返回动画
    NOT_MATCH,  // 忽略结果继续匹配
    SET_DEFAULT,  // 将结果设置为当前匹配的默认动画
    RETURN_DEFAULT  // 直接返回默认动画
}
