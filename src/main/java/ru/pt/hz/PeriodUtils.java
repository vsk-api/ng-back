package ru.pt.hz;

import java.time.LocalDate;
import java.time.Period;

public class PeriodUtils {
    public static int comparePeriods(Period p1, Period p2) {
        // Опорная дата
        LocalDate base = LocalDate.now();

        // Вычисляем реальное количество дней
        long days1 = base.plus(p1).toEpochDay() - base.toEpochDay();
        long days2 = base.plus(p2).toEpochDay() - base.toEpochDay();

        return Long.compare(days1, days2);
    }

    public static LocalDate addPeriod(LocalDate date, Period period) {
        return date.plus(period);
    }

    public static String addPeriod(String date, String period) {
        // Adds a Period to an ISO_OFFSET_DATE_TIME string and returns the result as ISO_OFFSET_DATE_TIME string
        if (date == null || period == null) {
            throw new IllegalArgumentException("date and period must not be null");
        }
        java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(date);
        Period p = Period.parse(period);
        java.time.OffsetDateTime result = odt.plus(p);
        return result.toString();
    }

    public static boolean isPeriodValid(String period) {
        return Period.parse(period) != null;
    }

    public static boolean isDateInRange(String date1, String date2, String range) {
        if (date1 == null || date2 == null || range == null) {
            throw new IllegalArgumentException("date1, date2, and range must not be null");
        }
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Range format is invalid, expected format: 'P0D-P15D'");
        }
        Period minPeriod, maxPeriod;
        try {
            minPeriod = Period.parse(parts[0]);
            maxPeriod = Period.parse(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Range period format is invalid, expected ISO-8601 period, e.g. 'P0D-P15D'");
        }
        java.time.OffsetDateTime d1, d2;
        try {
            d1 = java.time.OffsetDateTime.parse(date1);
            d2 = java.time.OffsetDateTime.parse(date2);
        } catch (Exception e) {
            throw new IllegalArgumentException("date1 or date2 is not a valid ISO_OFFSET_DATE_TIME string");
        }
        java.time.OffsetDateTime minDate = d1.plus(minPeriod);
        java.time.OffsetDateTime maxDate = d1.plus(maxPeriod);
        if (minDate.isAfter(d2) || maxDate.isBefore(d2)) {
            return false;
        }
        return true;

    }

    public static String isDatesInList(String date1, String date2, String list) {
        if (date1 == null || date2 == null || list == null) {
            throw new IllegalArgumentException("date1, date2, and list must not be null");
        }
        String[] parts = list.split(",");
    // Check if all elements of parts are valid periods
    for (String part : parts) {
        try {
            Period.parse(part.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid period in list: " + part);
        }
    }

    if (parts.length == 1) {
        // If only one period, return date1 + part[0]
        java.time.OffsetDateTime d1 = java.time.OffsetDateTime.parse(date1);
        Period p = Period.parse(parts[0].trim());
        java.time.OffsetDateTime result = d1.plus(p);
        return result.toString();
    }

    // Calculate period between date2 and date1
    java.time.OffsetDateTime d1 = java.time.OffsetDateTime.parse(date1);
    java.time.OffsetDateTime d2 = java.time.OffsetDateTime.parse(date2);

    // Calculate the period between d1 and d2
    Period between = Period.between(d1.toLocalDate(), d2.toLocalDate());

    // Check if the calculated period is in the list
    boolean found = false;
    for (String part : parts) {
        Period p = Period.parse(part.trim());
        if (p.equals(between)) {
            found = true;
            break;
        }
    }
    if (found) {
        return date1;
    } else {
        throw new IllegalArgumentException("The period between dates is not in the list of allowed periods");
    }
    }

    public static String getNextMonth(String date) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        java.time.OffsetDateTime d = java.time.OffsetDateTime.parse(date);
        java.time.OffsetDateTime result = d.plus(Period.parse("P1M"));
        // Set the day of month to 1
        result = result.withDayOfMonth(1);
        return result.toString();
    }
}
