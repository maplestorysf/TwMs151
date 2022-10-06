package tools;

import java.nio.charset.Charset;

public class StringUtil {

    public static final int getlength(final String str) {
        byte[] bt = str.getBytes(Charset.forName("BIG5"));
        return bt.length;
    }

    public static String getLeftPaddedStr(final String in, final char padchar, final int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int x = getlength(in); x < length; x++) {
            builder.append(padchar);
        }
        builder.append(in);
        return builder.toString();
    }

    public static String getRightPaddedStr(final String in, final char padchar, final int length) {
        StringBuilder builder = new StringBuilder(in);
        for (int x = getlength(in); x < length; x++) {
            builder.append(padchar);
        }
        return builder.toString();
    }

    public static String joinStringFrom(final String arr[], final int start) {
        return joinStringFrom(arr, start, " ");
    }

    public static String joinStringFrom(final String arr[], final int start, final String sep) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(sep);
            }
        }
        return builder.toString();
    }

    public static String makeEnumHumanReadable(final String enumName) {
        StringBuilder builder = new StringBuilder(enumName.length() + 1);
        for (String word : enumName.split("_")) {
            if (word.length() <= 2) {
                builder.append(word); // assume that it's an abbrevation
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, enumName.length());
    }

    public static int countCharacters(final String str, final char chr) {
        int ret = 0;
        for (int i = 0; i < getlength(str); i++) {
            if (str.charAt(i) == chr) {
                ret++;
            }
        }
        return ret;
    }

    public static String getReadableMillis(long startMillis, long endMillis) {
        StringBuilder sb = new StringBuilder();
        double elapsedSeconds = (endMillis - startMillis) / 1000.0;
        int elapsedSecs = ((int) elapsedSeconds) % 60;
        int elapsedMinutes = (int) (elapsedSeconds / 60.0);
        int elapsedMins = elapsedMinutes % 60;
        int elapsedHrs = elapsedMinutes / 60;
        int elapsedHours = elapsedHrs % 24;
        int elapsedDays = elapsedHrs / 24;
        if (elapsedDays > 0) {
            boolean mins = elapsedHours > 0;
            sb.append(elapsedDays);
            sb.append(" day").append(elapsedDays > 1 ? "s" : "").append(mins ? ", " : ".");
            if (mins) {
                boolean secs = elapsedMins > 0;
                if (!secs) {
                    sb.append("and ");
                }
                sb.append(elapsedHours);
                sb.append(" hour").append(elapsedHours > 1 ? "s" : "").append(secs ? ", " : ".");
                if (secs) {
                    boolean millis = elapsedSecs > 0;
                    if (!millis) {
                        sb.append("and ");
                    }
                    sb.append(elapsedMins);
                    sb.append(" minute").append(elapsedMins > 1 ? "s" : "").append(millis ? ", " : ".");
                    if (millis) {
                        sb.append("and ");
                        sb.append(elapsedSecs);
                        sb.append(" second").append(elapsedSecs > 1 ? "s" : "").append(".");
                    }
                }
            }
        } else if (elapsedHours > 0) {
            boolean mins = elapsedMins > 0;
            sb.append(elapsedHours);
            sb.append(" hour").append(elapsedHours > 1 ? "s" : "").append(mins ? ", " : ".");
            if (mins) {
                boolean secs = elapsedSecs > 0;
                if (!secs) {
                    sb.append("and ");
                }
                sb.append(elapsedMins);
                sb.append(" minute").append(elapsedMins > 1 ? "s" : "").append(secs ? ", " : ".");
                if (secs) {
                    sb.append("and ");
                    sb.append(elapsedSecs);
                    sb.append(" second").append(elapsedSecs > 1 ? "s" : "").append(".");
                }
            }
        } else if (elapsedMinutes > 0) {
            boolean secs = elapsedSecs > 0;
            sb.append(elapsedMinutes);
            sb.append(" minute").append(elapsedMinutes > 1 ? "s" : "").append(secs ? " " : ".");
            if (secs) {
                sb.append("and ");
                sb.append(elapsedSecs);
                sb.append(" second").append(elapsedSecs > 1 ? "s" : "").append(".");
            }
        } else if (elapsedSeconds > 0) {
            sb.append((int) elapsedSeconds);
            sb.append(" second").append(elapsedSeconds > 1 ? "s" : "").append(".");
        } else {
            sb.append("None.");
        }
        return sb.toString();
    }

    public static int getDaysAmount(long startMillis, long endMillis) {
        double elapsedSeconds = (endMillis - startMillis) / 1000.0;
        int elapsedMinutes = (int) (elapsedSeconds / 60.0);
        int elapsedHrs = elapsedMinutes / 60;
        int elapsedDays = elapsedHrs / 24;
        return elapsedDays;
    }

    public static String getReadableMillis_ch(long startMillis, long endMillis) {
        StringBuilder sb = new StringBuilder();
        double elapsedSeconds = (endMillis - startMillis) / 1000.0;
        int elapsedSecs = ((int) elapsedSeconds) % 60;
        int elapsedMinutes = (int) (elapsedSeconds / 60.0);
        int elapsedMins = elapsedMinutes % 60;
        int elapsedHrs = elapsedMinutes / 60;
        int elapsedHours = elapsedHrs % 24;
        int elapsedDays = elapsedHrs / 24;
        if (elapsedDays > 0) {
            boolean mins = elapsedHours > 0;
            sb.append(elapsedDays);
            sb.append(" 天");
            if (mins) {
                boolean secs = elapsedMins > 0;
                sb.append(elapsedHours);
                sb.append("小時");
                if (secs) {
                    boolean millis = elapsedSecs > 0;
                    sb.append(elapsedMins);
                    sb.append("分鐘");
                    if (millis) {
                        sb.append(elapsedSecs);
                        sb.append("秒");
                    }
                }
            }
        } else if (elapsedHours > 0) {
            boolean mins = elapsedMins > 0;
            sb.append(elapsedHours);
            sb.append("小時");
            if (mins) {
                boolean secs = elapsedSecs > 0;
                sb.append(elapsedMins);
                sb.append("分鐘");
                if (secs) {
                    sb.append(elapsedSecs);
                    sb.append("秒");
                }
            }
        } else if (elapsedMinutes > 0) {
            boolean secs = elapsedSecs > 0;
            sb.append(elapsedMinutes);
            sb.append("分鐘");
            if (secs) {
                sb.append(elapsedSecs);
                sb.append("秒");
            }
        } else if (elapsedSeconds > 0.0D) {
            sb.append((int) elapsedSeconds);
            sb.append("秒");
        } else {
            sb.append("None.");
        }
        return sb.toString();
    }

}
