package org.telegram.freetux.utils.shamsicalendar;

public class PersianCalendarUtils {
    public static long persianToJulian(long year, int month, int day) {
        return (((long) (month < 7 ? month * 31 : (month * 30) + 6)) + ((1029983 * ((long) Math.floor(((double) (year - 474)) / 2820.0d))) + (((365 * ((ceil((double) (year - 474), 2820.0d) + 474) - 1)) + ((long) Math.floor(((double) ((682 * (ceil((double) (year - 474), 2820.0d) + 474)) - 110)) / 2816.0d))) + 1948320))) + ((long) day);
    }

    public static boolean isPersianLeapYear(int persianYear) {
        return ceil((38.0d + ((double) (ceil((double) (((long) persianYear) - 474), 2820.0d) + 474))) * 682.0d, 2816.0d) < 682;
    }

    public static long julianToPersian(long julianDate) {
        long persianEpochInJulian = julianDate - persianToJulian(475, 0, 1);
        long cyear = ceil((double) persianEpochInJulian, 1029983.0d);
        long year = (474 + (2820 * ((long) Math.floor(((double) persianEpochInJulian) / 1029983.0d)))) + (cyear != 1029982 ? (long) Math.floor(((2816.0d * ((double) cyear)) + 1031337.0d) / 1028522.0d) : 2820);
        long aux = (1 + julianDate) - persianToJulian(year, 0, 1);
        int month = (int) (aux > 186 ? Math.ceil(((double) (aux - 6)) / 30.0d) - 1.0d : Math.ceil(((double) aux) / 31.0d) - 1.0d);
        return ((year << 16) | ((long) (month << 8))) | ((long) ((int) (julianDate - (persianToJulian(year, month, 1) - 1))));
    }

    public static long ceil(double double1, double double2) {
        return (long) (double1 - (Math.floor(double1 / double2) * double2));
    }
}