package cs246.scripturememorization;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class sfHelper {
    /**
     * Scripture Format Helper
     * contains static methods for formatting scripture class data
     */

    public static String getReference(Scripture s) {
        return String.format("%s %s:%s", s.book, s.chapter, s.verse);
    }

    public static String getTextShort(Scripture s) {
        int end = 35 > s.text.length() ? s.text.length() : 35;
        return s.text.substring(0,end) + "...";
    }

    public static String getDateReviewed(Date d) {
        return String.format("Last Reviewed : %s", getDate(d));
    }

    public static String getDateMemorized(Date d) {
        return String.format("Memorized : %s", getDate(d));
    }

    public static String getPercent(Scripture s) {
        String sReturn;
        sReturn = (s.percentCorrect < 10) ? " " : "";
        sReturn += (s.percentCorrect < 100) ? " " : "";
        sReturn += s.percentCorrect;
        sReturn += "%";
        return sReturn;
    }

    public static String getDate(Date d) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH");
        boolean pm = false;
        String hour = localDateFormat.format(d);
        int temp = Integer.parseInt(hour);
        if (temp > 12) {
            pm = true;
            temp -= 12;
        }

        SimpleDateFormat localMinuteFormat = new SimpleDateFormat(":MM");
        String time = temp + localMinuteFormat.format(d) + " ";
        time += (pm? "p.m." : "a.m.");
        return String.format(Locale.ENGLISH,
                "%s %d, %d %s",
                getMonth(d.getMonth()),
                d.getDate(),
                d.getYear() + 1900,
                time);
    }

    private static String getMonth(int month)
    {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "July";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "error";
        }
    }
}
