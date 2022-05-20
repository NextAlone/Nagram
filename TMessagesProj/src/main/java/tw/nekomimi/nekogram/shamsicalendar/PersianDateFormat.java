
package tw.nekomimi.nekogram.shamsicalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Saman on 3/31/2017 AD.
 */

public class PersianDateFormat {
  //variable
  /**
   * Key for convert Date to String
   */
  private String[] key = {"a", "l", "j", "F", "Y", "H", "i", "s", "d", "g", "n", "m", "t", "w", "y",
      "z", "A",
      "L","X","C","E","T","b","D","e","B","S","k"};
  private String pattern;
  /**
   * key_parse for convert String to PersianDate
   *
   * yyyy = Year (1396) MM = month (02-12-...) dd = day (13-02-15-...) HH = Hour (13-02-15-...) mm =
   * minutes (13-02-15-...) ss = second (13-02-15-...)
   */
  private String[] key_parse = {"yyyy", "MM", "dd", "HH", "mm", "ss"};

  /**
   * Constracutor
   */
  public PersianDateFormat(String pattern) {
    this.pattern = pattern;
  }

  /**
   * initilize pattern
   */
  public PersianDateFormat() {
    pattern = "l j F Y H:i:s";
  }




   /**
   * new patterns T for mounth name in latin ----- b for shortday in persian number ----- D for 4 digit year in persian number ---- e for short month in persian number
   */

   /**
   * new patterns k for day name in latin
   */



  public static String format(PersianDate date, String pattern) {
    if(pattern == null) pattern="l j F Y H:i:s";
    String[] key = {"a", "l", "j", "F", "Y", "H", "i", "s", "d", "g", "n", "m", "t", "w", "y", "z",
        "A", "L","X","C","E","T","b","D","e","B","S","k"};
    String year2;
    if (("" + date.getShYear()).length() == 2) {
      year2 = "" + date.getShYear();
    } else if (("" + date.getShYear()).length() == 3) {
      year2 = ("" + date.getShYear()).substring(2, 3);
    } else {
      year2 = ("" + date.getShYear()).substring(2, 4);
    }
    String[] values = {date.getShortTimeOfTheDay(), date.dayName(), "" + date.getShDay(),
        date.monthName(),
        "" + date.getShYear(),
        textNumberFilterStatic("" + date.getHour()), textNumberFilterStatic("" + date.getMinute()),
        textNumberFilterStatic("" + date.getSecond()),
        textNumberFilterStatic("" + date.getShDay()), "" + date.getHour(), "" + date.getShMonth(),
        textNumberFilterStatic("" + date.getShMonth()),
        "" + date.getMonthDays(), "" + date.dayOfWeek(), year2, "" + date.getDayInYear(),
        date.getTimeOfTheDay(),
        (date.isLeap() ? "1" : "0"),
        date.AfghanMonthName(),
        date.KurdishMonthName(),
        date.PashtoMonthName(),
        date.monthNamesLatin(),
        LanguageUtils.getPersianNumbers(String.valueOf(date.getShDay())),
        LanguageUtils.getPersianNumbers(String.valueOf(date.getShYear())),
        LanguageUtils.getPersianNumbers(String.valueOf( date.getShMonth())),
        LanguageUtils.getPersianNumbers(String.valueOf(textNumberFilterStatic("" + date.getHour()))),
        LanguageUtils.getPersianNumbers(String.valueOf(textNumberFilterStatic("" + date.getMinute())),
        date.latindayNames()) 

    };
    for (int i = 0; i < key.length; i++) {
      pattern = pattern.replace(key[i], values[i]);
    }
    return pattern;
  }

  public String format(PersianDate date) {
    String year2 = null;
    if (("" + date.getShYear()).length() == 2) {
      year2 = "" + date.getShYear();
    } else if (("" + date.getShYear()).length() == 3) {
      year2 = ("" + date.getShYear()).substring(2, 3);
    } else {
      year2 = ("" + date.getShYear()).substring(2, 4);
    }
    String[] values = {date.isMidNight() ? "ق.ظ" : "ب.ظ", date.dayName(), "" + date.getShDay(),
        date.monthName(),
        "" + date.getShYear(),
        this.textNumberFilter("" + date.getHour()), this.textNumberFilter("" + date.getMinute()),
        this.textNumberFilter("" + date.getSecond()),
        this.textNumberFilter("" + date.getShDay()), "" + date.getHour(), "" + date.getShMonth(),
        this.textNumberFilter("" + date.getShMonth()),
        "" + date.getMonthDays(), "" + date.dayOfWeek(), year2, "" + date.getDayInYear(),
        date.getTimeOfTheDay(),
        (date.isLeap() ? "1" : "0"),
        date.AfghanMonthName(),
        date.KurdishMonthName(),
        date.PashtoMonthName(),
        date.monthNamesLatin(),
        LanguageUtils.getPersianNumbers(String.valueOf(date.getShDay())),
        LanguageUtils.getPersianNumbers(String.valueOf(date.getShYear())),
        LanguageUtils.getPersianNumbers(String.valueOf( date.getShMonth())),
        LanguageUtils.getPersianNumbers(String.valueOf(textNumberFilterStatic("" + date.getHour()))),
        LanguageUtils.getPersianNumbers(String.valueOf(textNumberFilterStatic("" + date.getMinute())),
        date.latindayNames())
    };
    return this.stringUtils(this.pattern, this.key, values);
  }

  /**
   * Parse jalli date from String
   *
   * @param date date in string
   */
  public PersianDate parse(String date) throws ParseException {
    return this.parse(date, this.pattern);
  }

  /**
   * Parse jalli date from String
   *
   * @param date date in string
   * @param pattern pattern
   */
  public PersianDate parse(String date, String pattern) throws ParseException {
    ArrayList<Integer> JalaliDate = new ArrayList<Integer>() {{
      add(0);
      add(0);
      add(0);
      add(0);
      add(0);
      add(0);
    }};
    for (int i = 0; i < key_parse.length; i++) {
      if ((pattern.contains(key_parse[i]))) {
        int start_temp = pattern.indexOf(key_parse[i]);
        int end_temp = start_temp + key_parse[i].length();
        String dateReplace = date.substring(start_temp, end_temp);
        if (dateReplace.matches("[-+]?\\d*\\.?\\d+")) {
          JalaliDate.set(i, Integer.parseInt(dateReplace));
        } else {
          throw new ParseException("Parse Exception", 10);
        }
      }
    }
    return new PersianDate()
        .initJalaliDate(JalaliDate.get(0), JalaliDate.get(1), JalaliDate.get(2), JalaliDate.get(3),
            JalaliDate.get(4), JalaliDate.get(5));
  }

  /**
   * Convert String Grg date to persiand date object
   *
   * @param date date in String
   * @return PersianDate object
   */
  public PersianDate parseGrg(String date) throws ParseException {
    return this.parseGrg(date, this.pattern);
  }

  /**
   * Convert String Grg date to persiand date object
   *
   * @param date date String
   * @param pattern pattern
   * @return PersianDate object
   */
  public PersianDate parseGrg(String date, String pattern) throws ParseException {
    Date dateInGrg = new SimpleDateFormat(pattern).parse(date);
    return new PersianDate(dateInGrg.getTime());
  }

  /**
   * Replace String
   *
   * @param text String
   * @param key Loking for
   * @param values Replace with
   */
  private String stringUtils(String text, String[] key, String[] values) {
    for (int i = 0; i < key.length; i++) {
      text = text.replace(key[i], values[i]);
    }
    return text;
  }

  /**
   * add zero to start
   *
   * @param date data
   * @return return string with 0 in start
   */
  private String textNumberFilter(String date) {
    if (date.length() < 2) {
      return "0" + date;
    }
    return date;
  }

  public static String textNumberFilterStatic(String date) {
    if (date.length() < 2) {
      return "0" + date;
    }
    return date;
  }
}
