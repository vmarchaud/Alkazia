package com.massivecraft.factions.util;

import java.util.Map.Entry;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.util.TextUtil;

public class HealthBarUtil {
    public static String getHealthbar(double healthQuota, final int barLength) {
        // Ensure between 0 and 1;
        healthQuota = HealthBarUtil.fixQuota(healthQuota);

        // What color is the health bar?
        final String color = HealthBarUtil.getColorFromHealthQuota(healthQuota);

        // how much solid should there be?
        final int solidCount = (int) Math.ceil(barLength * healthQuota);

        // The rest is empty
        final int emptyCount = (int) ((barLength - solidCount) / Conf.spoutHealthBarSolidsPerEmpty);

        // Create the non-parsed bar
        String ret = Conf.spoutHealthBarLeft + TextUtil.repeat(Conf.spoutHealthBarSolid, solidCount) + Conf.spoutHealthBarBetween + TextUtil.repeat(Conf.spoutHealthBarEmpty, emptyCount) + Conf.spoutHealthBarRight;

        // Replace color tag
        ret = ret.replace("{c}", color);

        // Parse amp color codes
        ret = TextUtil.parseColorAmp(ret);

        return ret;
    }

    public static String getHealthbar(final double healthQuota) {
        return HealthBarUtil.getHealthbar(healthQuota, Conf.spoutHealthBarWidth);
    }

    public static double fixQuota(final double healthQuota) {
        if (healthQuota > 1) return 1d;
        else if (healthQuota < 0) return 0d;
        return healthQuota;
    }

    public static String getColorFromHealthQuota(final double healthQuota) {
        Double currentRoof = null;
        String ret = null;
        for (final Entry<Double, String> entry : Conf.spoutHealthBarColorUnderQuota.entrySet()) {
            final double roof = entry.getKey();
            final String color = entry.getValue();
            if (healthQuota <= roof && (currentRoof == null || roof <= currentRoof)) {
                currentRoof = roof;
                ret = color;
            }
        }
        return ret;
    }
}