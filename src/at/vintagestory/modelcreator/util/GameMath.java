package at.vintagestory.modelcreator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GameMath
{
    public static float TWOPI = (float)Math.PI * 2;
    public static float PI = (float)Math.PI;
    public static float PIHALF = (float)Math.PI / 2;

    public static float DEG2RAD = (float)Math.PI / 180.0f;
    public static float RAD2DEG = 180.0f / (float)Math.PI;

    public static float round(float num, int digits)
    {
        BigDecimal decimal = new BigDecimal(num).setScale(digits, RoundingMode.HALF_EVEN);
        return decimal.floatValue();
    }

    public static double round(double num, int digits)
    {
        BigDecimal decimal = new BigDecimal(num).setScale(digits, RoundingMode.HALF_EVEN);
        return decimal.doubleValue();
    }
}
