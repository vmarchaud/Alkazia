package net.minecraft.util;

import java.util.Random;

public class MathHelper
{
    /**
     * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
     */
    private static float[] SIN_TABLE = new float[65536];

    /**
     * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;
    private static final String __OBFID = "CL_00001496";

    /**
     * sin looked up in a table
     */
    public static final float sin(float p_76126_0_)
    {
        return SIN_TABLE[(int)(p_76126_0_ * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static final float cos(float p_76134_0_)
    {
        return SIN_TABLE[(int)(p_76134_0_ * 10430.378F + 16384.0F) & 65535];
    }

    public static final float sqrt_float(float p_76129_0_)
    {
        return (float)Math.sqrt((double)p_76129_0_);
    }

    public static final float sqrt_double(double p_76133_0_)
    {
        return (float)Math.sqrt(p_76133_0_);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float p_76141_0_)
    {
        int var1 = (int)p_76141_0_;
        return p_76141_0_ < (float)var1 ? var1 - 1 : var1;
    }

    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    public static int truncateDoubleToInt(double p_76140_0_)
    {
        return (int)(p_76140_0_ + 1024.0D) - 1024;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double p_76128_0_)
    {
        int var2 = (int)p_76128_0_;
        return p_76128_0_ < (double)var2 ? var2 - 1 : var2;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double p_76124_0_)
    {
        long var2 = (long)p_76124_0_;
        return p_76124_0_ < (double)var2 ? var2 - 1L : var2;
    }

    public static int func_154353_e(double p_154353_0_)
    {
        return (int)(p_154353_0_ >= 0.0D ? p_154353_0_ : -p_154353_0_ + 1.0D);
    }

    public static float abs(float p_76135_0_)
    {
        return p_76135_0_ >= 0.0F ? p_76135_0_ : -p_76135_0_;
    }

    /**
     * Returns the unsigned value of an int.
     */
    public static int abs_int(int p_76130_0_)
    {
        return p_76130_0_ >= 0 ? p_76130_0_ : -p_76130_0_;
    }

    public static int ceiling_float_int(float p_76123_0_)
    {
        int var1 = (int)p_76123_0_;
        return p_76123_0_ > (float)var1 ? var1 + 1 : var1;
    }

    public static int ceiling_double_int(double p_76143_0_)
    {
        int var2 = (int)p_76143_0_;
        return p_76143_0_ > (double)var2 ? var2 + 1 : var2;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int p_76125_0_, int p_76125_1_, int p_76125_2_)
    {
        return p_76125_0_ < p_76125_1_ ? p_76125_1_ : (p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float p_76131_0_, float p_76131_1_, float p_76131_2_)
    {
        return p_76131_0_ < p_76131_1_ ? p_76131_1_ : (p_76131_0_ > p_76131_2_ ? p_76131_2_ : p_76131_0_);
    }

    public static double clamp_double(double p_151237_0_, double p_151237_2_, double p_151237_4_)
    {
        return p_151237_0_ < p_151237_2_ ? p_151237_2_ : (p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_);
    }

    public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_)
    {
        return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    /**
     * Maximum of the absolute value of two numbers.
     */
    public static double abs_max(double p_76132_0_, double p_76132_2_)
    {
        if (p_76132_0_ < 0.0D)
        {
            p_76132_0_ = -p_76132_0_;
        }

        if (p_76132_2_ < 0.0D)
        {
            p_76132_2_ = -p_76132_2_;
        }

        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    /**
     * Buckets an integer with specifed bucket sizes.  Args: i, bucketSize
     */
    public static int bucketInt(int p_76137_0_, int p_76137_1_)
    {
        return p_76137_0_ < 0 ? -((-p_76137_0_ - 1) / p_76137_1_) - 1 : p_76137_0_ / p_76137_1_;
    }

    /**
     * Tests if a string is null or of length zero
     */
    public static boolean stringNullOrLengthZero(String p_76139_0_)
    {
        return p_76139_0_ == null || p_76139_0_.length() == 0;
    }

    public static int getRandomIntegerInRange(Random p_76136_0_, int p_76136_1_, int p_76136_2_)
    {
        return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_)
    {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random p_82716_0_, double p_82716_1_, double p_82716_3_)
    {
        return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
    }

    public static double average(long[] p_76127_0_)
    {
        long var1 = 0L;
        long[] var3 = p_76127_0_;
        int var4 = p_76127_0_.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            long var6 = var3[var5];
            var1 += var6;
        }

        return (double)var1 / (double)p_76127_0_.length;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float p_76142_0_)
    {
        p_76142_0_ %= 360.0F;

        if (p_76142_0_ >= 180.0F)
        {
            p_76142_0_ -= 360.0F;
        }

        if (p_76142_0_ < -180.0F)
        {
            p_76142_0_ += 360.0F;
        }

        return p_76142_0_;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double p_76138_0_)
    {
        p_76138_0_ %= 360.0D;

        if (p_76138_0_ >= 180.0D)
        {
            p_76138_0_ -= 360.0D;
        }

        if (p_76138_0_ < -180.0D)
        {
            p_76138_0_ += 360.0D;
        }

        return p_76138_0_;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails
     */
    public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_)
    {
        int var2 = p_82715_1_;

        try
        {
            var2 = Integer.parseInt(p_82715_0_);
        }
        catch (Throwable var4)
        {
            ;
        }

        return var2;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
     */
    public static int parseIntWithDefaultAndMax(String p_82714_0_, int p_82714_1_, int p_82714_2_)
    {
        int var3 = p_82714_1_;

        try
        {
            var3 = Integer.parseInt(p_82714_0_);
        }
        catch (Throwable var5)
        {
            ;
        }

        if (var3 < p_82714_2_)
        {
            var3 = p_82714_2_;
        }

        return var3;
    }

    /**
     * parses the string as double or returns the second parameter if it fails.
     */
    public static double parseDoubleWithDefault(String p_82712_0_, double p_82712_1_)
    {
        double var3 = p_82712_1_;

        try
        {
            var3 = Double.parseDouble(p_82712_0_);
        }
        catch (Throwable var6)
        {
            ;
        }

        return var3;
    }

    public static double parseDoubleWithDefaultAndMax(String p_82713_0_, double p_82713_1_, double p_82713_3_)
    {
        double var5 = p_82713_1_;

        try
        {
            var5 = Double.parseDouble(p_82713_0_);
        }
        catch (Throwable var8)
        {
            ;
        }

        if (var5 < p_82713_3_)
        {
            var5 = p_82713_3_;
        }

        return var5;
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    public static int roundUpToPowerOfTwo(int p_151236_0_)
    {
        int var1 = p_151236_0_ - 1;
        var1 |= var1 >> 1;
        var1 |= var1 >> 2;
        var1 |= var1 >> 4;
        var1 |= var1 >> 8;
        var1 |= var1 >> 16;
        return var1 + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    private static boolean isPowerOfTwo(int p_151235_0_)
    {
        return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
     * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
     * then subtract 1 from the return value.
     */
    private static int calculateLogBaseTwoDeBruijn(int p_151241_0_)
    {
        p_151241_0_ = isPowerOfTwo(p_151241_0_) ? p_151241_0_ : roundUpToPowerOfTwo(p_151241_0_);
        return multiplyDeBruijnBitPosition[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
    }

    /**
     * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
     * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
     */
    public static int calculateLogBaseTwo(int p_151239_0_)
    {
        return calculateLogBaseTwoDeBruijn(p_151239_0_) - (isPowerOfTwo(p_151239_0_) ? 0 : 1);
    }

    public static int func_154354_b(int p_154354_0_, int p_154354_1_)
    {
        if (p_154354_1_ == 0)
        {
            return 0;
        }
        else
        {
            if (p_154354_0_ < 0)
            {
                p_154354_1_ *= -1;
            }

            int var2 = p_154354_0_ % p_154354_1_;
            return var2 == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - var2;
        }
    }

    static
    {
        for (int var0 = 0; var0 < 65536; ++var0)
        {
            SIN_TABLE[var0] = (float)Math.sin((double)var0 * Math.PI * 2.0D / 65536.0D);
        }

        multiplyDeBruijnBitPosition = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    }
}
