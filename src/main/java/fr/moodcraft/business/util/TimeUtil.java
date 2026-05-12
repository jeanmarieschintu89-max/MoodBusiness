package fr.moodcraft.business.util;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class TimeUtil {

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm"
            );

    private TimeUtil() {}

    public static String formatDate(
            long time
    ) {

        if (time <= 0) {
            return "Jamais";
        }

        return FORMAT.format(
                new Date(time)
        );
    }

    public static String compactDuration(
            long millis
    ) {

        if (millis <= 0) {
            return "0s";
        }

        long days =
                TimeUnit.MILLISECONDS.toDays(millis);

        millis -=
                TimeUnit.DAYS.toMillis(days);

        long hours =
                TimeUnit.MILLISECONDS.toHours(millis);

        millis -=
                TimeUnit.HOURS.toMillis(hours);

        long minutes =
                TimeUnit.MILLISECONDS.toMinutes(millis);

        if (days > 0) {
            return days + "j " + hours + "h";
        }

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        return minutes + "m";
    }

    public static long hours(
            int hours
    ) {

        return TimeUnit.HOURS.toMillis(hours);
    }

    public static long days(
            int days
    ) {

        return TimeUnit.DAYS.toMillis(days);
    }
}