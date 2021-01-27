package at.vintagestory.modelcreator.util;

public class VecUtil {
    public static void Round(float[] array, int digits) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = GameMath.round(array[i], digits);
        }
    }

    public static void Round(double[] array, int digits) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = GameMath.round(array[i], digits);
        }
    }
}
