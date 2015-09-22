package com.massivecraft.factions.zcore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TextUtil {
    public Map<String, String> tags;

    public TextUtil() {
        this.tags = new HashMap<String, String>();
    }

    // -------------------------------------------- //
    // Top-level parsing functions.
    // -------------------------------------------- //

    public String parse(final String str, final Object... args) {
        return String.format(this.parse(str), args);
    }

    public String parse(final String str) {
        return this.parseTags(TextUtil.parseColor(str));
    }

    // -------------------------------------------- //
    // Tag parsing
    // -------------------------------------------- //

    public String parseTags(final String str) {
        return TextUtil.replaceTags(str, this.tags);
    }

    public static final transient Pattern patternTag = Pattern.compile("<([a-zA-Z0-9_]*)>");

    public static String replaceTags(final String str, final Map<String, String> tags) {
        final StringBuffer ret = new StringBuffer();
        final Matcher matcher = TextUtil.patternTag.matcher(str);
        while (matcher.find()) {
            final String tag = matcher.group(1);
            final String repl = tags.get(tag);
            if (repl == null) {
                matcher.appendReplacement(ret, "<" + tag + ">");
            } else {
                matcher.appendReplacement(ret, repl);
            }
        }
        matcher.appendTail(ret);
        return ret.toString();
    }

    // -------------------------------------------- //
    // Color parsing
    // -------------------------------------------- //

    public static String parseColor(String string) {
        string = TextUtil.parseColorAmp(string);
        string = TextUtil.parseColorAcc(string);
        string = TextUtil.parseColorTags(string);
        return string;
    }

    public static String parseColorAmp(String string) {
        string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
        string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
        string = string.replace("&&", "&");
        return string;
    }

    public static String parseColorAcc(final String string) {
        return string.replace("`e", "").replace("`r", ChatColor.RED.toString()).replace("`R", ChatColor.DARK_RED.toString()).replace("`y", ChatColor.YELLOW.toString()).replace("`Y", ChatColor.GOLD.toString()).replace("`g", ChatColor.GREEN.toString()).replace("`G", ChatColor.DARK_GREEN.toString()).replace("`a", ChatColor.AQUA.toString()).replace("`A", ChatColor.DARK_AQUA.toString()).replace("`b", ChatColor.BLUE.toString()).replace("`B", ChatColor.DARK_BLUE.toString()).replace("`p", ChatColor.LIGHT_PURPLE.toString()).replace("`P", ChatColor.DARK_PURPLE.toString()).replace("`k", ChatColor.BLACK.toString()).replace("`s", ChatColor.GRAY.toString()).replace("`S", ChatColor.DARK_GRAY.toString()).replace("`w", ChatColor.WHITE.toString());
    }

    public static String parseColorTags(final String string) {
        return string.replace("<empty>", "").replace("<black>", "\u00A70").replace("<navy>", "\u00A71").replace("<green>", "\u00A72").replace("<teal>", "\u00A73").replace("<red>", "\u00A74").replace("<purple>", "\u00A75").replace("<gold>", "\u00A76").replace("<silver>", "\u00A77").replace("<gray>", "\u00A78").replace("<blue>", "\u00A79").replace("<lime>", "\u00A7a").replace("<aqua>", "\u00A7b").replace("<rose>", "\u00A7c").replace("<pink>", "\u00A7d").replace("<yellow>", "\u00A7e").replace("<white>", "\u00A7f");
    }

    // -------------------------------------------- //
    // Standard utils like UCFirst, implode and repeat.
    // -------------------------------------------- //

    public static String upperCaseFirst(final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String repeat(final String s, final int times) {
        if (times <= 0) return "";
        else return s + TextUtil.repeat(s, times - 1);
    }

    public static String implode(final List<String> list, final String glue) {
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                ret.append(glue);
            }
            ret.append(list.get(i));
            if(i > 30) {
            	ret.append("<i> et d'autres ...");
            	break;
            }
        }
        return ret.toString();
    }

    public static String implodeCommaAnd(final List<String> list, final String comma, final String and) {
        if (list.size() == 0) return "";
        if (list.size() == 1) return list.get(0);

        final String lastItem = list.get(list.size() - 1);
        final String nextToLastItem = list.get(list.size() - 2);
        final String merge = nextToLastItem + and + lastItem;
        list.set(list.size() - 2, merge);
        list.remove(list.size() - 1);

        return TextUtil.implode(list, comma);
    }

    public static String implodeCommaAnd(final List<String> list) {
        return TextUtil.implodeCommaAnd(list, ", ", " and ");
    }

    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    public static String getMaterialName(final Material material) {
        return material.toString().replace('_', ' ').toLowerCase();
    }

    public static String getMaterialName(final int materialId) {
        return TextUtil.getMaterialName(Material.getMaterial(materialId));
    }

    // -------------------------------------------- //
    // Paging and chrome-tools like titleize
    // -------------------------------------------- //

    private final static String titleizeLine = TextUtil.repeat("_", 52);
    private final static int titleizeBalance = -1;

    public String titleize(final String str) {
        final String center = ".[ " + this.parseTags("<l>") + str + this.parseTags("<a>") + " ].";
        final int centerlen = ChatColor.stripColor(center).length();
        final int pivot = TextUtil.titleizeLine.length() / 2;
        final int eatLeft = centerlen / 2 - TextUtil.titleizeBalance;
        final int eatRight = centerlen - eatLeft + TextUtil.titleizeBalance;

        if (eatLeft < pivot) return this.parseTags("<a>") + TextUtil.titleizeLine.substring(0, pivot - eatLeft) + center + TextUtil.titleizeLine.substring(pivot + eatRight);
        else return this.parseTags("<a>") + center;
    }

    public ArrayList<String> getPage(final List<String> lines, final int pageHumanBased, final String title) {
        final ArrayList<String> ret = new ArrayList<String>();
        final int pageZeroBased = pageHumanBased - 1;
        final int pageheight = 9;
        final int pagecount = lines.size() / pageheight + 1;

        ret.add(this.titleize(title + " " + pageHumanBased + "/" + pagecount));

        if (pagecount == 0) {
            ret.add(this.parseTags("<i>Sorry. No Pages available."));
            return ret;
        } else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(this.parseTags("<i>Invalid page. Must be between 1 and " + pagecount));
            return ret;
        }

        final int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        ret.addAll(lines.subList(from, to));

        return ret;
    }

    // -------------------------------------------- //
    // Describing Time
    // -------------------------------------------- //

    /**
     * Using this function you transform a delta in milliseconds
     * to a String like "2 weeks from now" or "7 days ago".
     */
    public static final long millisPerSecond = 1000;
    public static final long millisPerMinute = 60 * TextUtil.millisPerSecond;
    public static final long millisPerHour = 60 * TextUtil.millisPerMinute;
    public static final long millisPerDay = 24 * TextUtil.millisPerHour;
    public static final long millisPerWeek = 7 * TextUtil.millisPerDay;
    public static final long millisPerMonth = 31 * TextUtil.millisPerDay;
    public static final long millisPerYear = 365 * TextUtil.millisPerDay;

    public static Map<String, Long> unitMillis;

    static {
        TextUtil.unitMillis = new LinkedHashMap<String, Long>();
        TextUtil.unitMillis.put("years", TextUtil.millisPerYear);
        TextUtil.unitMillis.put("months", TextUtil.millisPerMonth);
        TextUtil.unitMillis.put("weeks", TextUtil.millisPerWeek);
        TextUtil.unitMillis.put("days", TextUtil.millisPerDay);
        TextUtil.unitMillis.put("hours", TextUtil.millisPerHour);
        TextUtil.unitMillis.put("minutes", TextUtil.millisPerMinute);
        TextUtil.unitMillis.put("seconds", TextUtil.millisPerSecond);
    }

    public static String getTimeDeltaDescriptionRelNow(final long millis) {
        String ret = "";

        double millisLeft = Math.abs(millis);

        final List<String> unitCountParts = new ArrayList<String>();
        for (final Entry<String, Long> entry : TextUtil.unitMillis.entrySet()) {
            if (unitCountParts.size() == 3) {
                break;
            }
            final String unitName = entry.getKey();
            final long unitSize = entry.getValue();
            final long unitCount = (long) Math.floor(millisLeft / unitSize);
            if (unitCount < 1) {
                continue;
            }
            millisLeft -= unitSize * unitCount;
            unitCountParts.add(unitCount + " " + unitName);
        }

        if (unitCountParts.size() == 0) return "just now";

        ret += TextUtil.implodeCommaAnd(unitCountParts);
        ret += " ";
        if (millis <= 0) {
            ret += "ago";
        } else {
            ret += "from now";
        }

        return ret;
    }

    // -------------------------------------------- //
    // String comparison
    // -------------------------------------------- //

    public static String getBestStartWithCI(final Collection<String> candidates, String start) {
        String ret = null;
        int best = 0;

        start = start.toLowerCase();
        final int minlength = start.length();
        for (final String candidate : candidates) {
            if (candidate.length() < minlength) {
                continue;
            }
            if (!candidate.toLowerCase().startsWith(start)) {
                continue;
            }

            // The closer to zero the better
            final int lendiff = candidate.length() - minlength;
            if (lendiff == 0) return candidate;
            if (lendiff < best || best == 0) {
                best = lendiff;
                ret = candidate;
            }
        }
        return ret;
    }
}
