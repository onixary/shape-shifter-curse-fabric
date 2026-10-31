package net.onixary.shapeShifterCurseFabric.additional_power;

/** ./gradlew checkFootstepSoundRhythm; no running Minecraft client required. */
public final class FootstepSoundRhythmCheck {
    public static void main(String[] args) {
        for (float speed : new float[]{0.45f, 1.0f, 1.5f}) {
            for (float base : new float[]{1.0f, 0.75f}) {
                for (float offset : new float[]{0.0f, 0.5f, 0.99f, 1.0f, 2.0f, -1.0f, Float.NaN}) {
                    var power = new ModifyFootstepSoundSpeedPower(null, null, speed, false, 1.0f, offset);
                    float shortGap = power.getStepSoundDistance(null, base, false);
                    float longGap = power.getStepSoundDistance(null, base, true);
                    float expectedOffset = Float.isNaN(offset) ? 0.0f : Math.max(0.0f, Math.min(0.99f, offset));
                    checkClose(shortGap, base / speed * (1.0f - expectedOffset), "Short gap / offset clamp");
                    checkClose(longGap, base / speed * (1.0f + expectedOffset), "Long gap / offset clamp");
                    checkClose(shortGap + longGap, 2.0f * base / speed, "Preserve total pair distance");
                    checkClose(power.getStepSoundDistance(null, base, false), shortGap, "Repeated PF queries must not advance phase");
                    if (!(shortGap > 0.0f && longGap >= shortGap)) throw new AssertionError("Gaps must stay positive and ordered");
                }
            }
        }
        System.out.println("Footstep sound rhythm checks passed.");
    }

    private static void checkClose(float actual, float expected, String message) {
        if (!(Math.abs(actual - expected) < 0.00001f)) throw new AssertionError(message + ": " + actual + " != " + expected);
    }
}
