package org.telegram.freetux.utils.shamsicalendar;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class PersianCalendar extends GregorianCalendar {
    private static final long serialVersionUID = 5541422440580682494L;
    private String delimiter = "/";
    private int persianDay;
    private int persianMonth;
    private int persianYear;

    private long convertToMilis(long julianDate) {
        return ((86400000 * julianDate) - 210866803200000L) + PersianCalendarUtils.ceil((double) (getTimeInMillis() - -210866803200000L), 8.64E7d);
    }

    public PersianCalendar(long millis) {
        setTimeInMillis(millis);
    }

    public PersianCalendar() {
        setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    protected void calculatePersianDate() {
        long PersianRowDate = PersianCalendarUtils.julianToPersian(((long) Math.floor((double) (getTimeInMillis() - -210866803200000L))) / 86400000);
        long year = PersianRowDate >> 16;
        int month = ((int) (65280 & PersianRowDate)) >> 8;
        int day = (int) (255 & PersianRowDate);
        if (year <= 0) {
            year--;
        }
        this.persianYear = (int) year;
        this.persianMonth = month;
        this.persianDay = day;
    }

    public boolean isPersianLeapYear() {
        return PersianCalendarUtils.isPersianLeapYear(this.persianYear);
    }

    public void setPersianDate(int persianYear, int persianMonth, int persianDay) {
        persianMonth++;
        this.persianYear = persianYear;
        this.persianMonth = persianMonth;
        this.persianDay = persianDay;
        setTimeInMillis(convertToMilis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? (long) this.persianYear : (long) (this.persianYear + 1), this.persianMonth - 1, this.persianDay)));
    }

    public int getPersianYear() {
        return this.persianYear;
    }

    public int getPersianMonth() {
        return this.persianMonth;
    }

    public String getPersianMonthName() {
        return PersianCalendarConstants.persianMonthNames[this.persianMonth];
    }

    public int getPersianDay() {
        return this.persianDay;
    }

    public String getPersianDayfanum() {
        return LanguageUtils.getPersianNumbers(String.valueOf(this.persianDay));
    }

    public String getPersianWeekDayName() {
        switch (get(7)) {
            case 1:
                return PersianCalendarConstants.persianWeekDays[1];
            case 2:
                return PersianCalendarConstants.persianWeekDays[2];
            case 3:
                return PersianCalendarConstants.persianWeekDays[3];
            case 4:
                return PersianCalendarConstants.persianWeekDays[4];
            case 5:
                return PersianCalendarConstants.persianWeekDays[5];
            case 7:
                return PersianCalendarConstants.persianWeekDays[0];
            default:
                return PersianCalendarConstants.persianWeekDays[6];
        }
    }

    public String getPersianLongDate() {
        return getPersianWeekDayName() + "  " + getPersianDayfanum() + "  " + getPersianMonthName() + "  " + this.persianYear;
    }


    public String getPersianNormalDate() {
        return getPersianDayfanum() + "  " + getPersianMonthName() + "  " + getPersianDayfanum();
    }
     //like 9 شهریور
public String getPersianMonthDay() {
        return getPersianDayfanum() + "  " + getPersianMonthName();
    }  
    public String getPersianLongDateAndTime() {
        return getPersianLongDate() + " ساعت " + get(11) + ":" + get(12) + ":" + get(13);
    }

    public String getPersianShortDate() {
        return "" + formatToMilitary(this.persianYear) + this.delimiter + formatToMilitary(getPersianMonth() + 1) + this.delimiter + formatToMilitary(this.persianDay);
    }

    public String getPersianShortDateTime() {
        return "" + formatToMilitary(this.persianYear) + this.delimiter + formatToMilitary(getPersianMonth() + 1) + this.delimiter + formatToMilitary(this.persianDay) + " " + formatToMilitary(get(11)) + ":" + formatToMilitary(get(12)) + ":" + formatToMilitary(get(13));
    }

    private String formatToMilitary(int i) {
        return i < 9 ? "0" + i : String.valueOf(i);
    }

    public void addPersianDate(int field, int amount) {
        if (amount != 0) {
            if (field < 0 || field >= 15) {
                throw new IllegalArgumentException();
            } else if (field == 1) {
                setPersianDate(this.persianYear + amount, getPersianMonth() + 1, this.persianDay);
            } else if (field == 2) {
                setPersianDate(this.persianYear + (((getPersianMonth() + 1) + amount) / 12), ((getPersianMonth() + 1) + amount) % 12, this.persianDay);
            } else {
                add(field, amount);
                calculatePersianDate();
            }
        }
    }

    public void parse(String dateString) {
        PersianCalendar p = new PersianDateParser(dateString, this.delimiter).getPersianDate();
        setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String toString() {
        String str = super.toString();
        return str.substring(0, str.length() - 1) + ",PersianDate=" + getPersianShortDate() + "]";
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void set(int field, int value) {
        super.set(field, value);
        calculatePersianDate();
    }

    public void setTimeInMillis(long millis) {
        super.setTimeInMillis(millis);
        calculatePersianDate();
    }

    public void setTimeZone(TimeZone zone) {
        super.setTimeZone(zone);
        calculatePersianDate();
    }
}