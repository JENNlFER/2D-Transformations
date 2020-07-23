package jen.util;

import java.text.DecimalFormat;

public class Format {

    private static final DecimalFormat format = new DecimalFormat("##0.##");
    private static final DecimalFormat colorFormat = new DecimalFormat("000");
    private static final DecimalFormat outputFormat = new DecimalFormat("000");

    public static double strToNum(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public static String numToStr(double d) {
        return format.format(d);
    }

    public static String color(double d) {
        d = d * 255;
        return colorFormat.format((int) d);
    }

    public static String out(double d) {
        return outputFormat.format(d);
    }
}
