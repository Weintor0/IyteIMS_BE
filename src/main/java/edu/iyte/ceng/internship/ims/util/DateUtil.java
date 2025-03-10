package edu.iyte.ceng.internship.ims.util;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {

    private static ZonedDateTime frozen;
    @Getter
    private static final ZoneId zoneId = ZoneId.of("UTC");

    public static ZonedDateTime asZonedDateTime(Date date) {
        return date != null ? Instant.ofEpochMilli(date.getTime()).atZone(zoneId) : null;
    }

    public static Date asDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static ZonedDateTime now() {
        if (frozen != null) {
            return frozen;
        }
        return ZonedDateTime.now(zoneId);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static void freeze() {
        freeze(now());
    }

    public static void freeze(ZonedDateTime zonedDateTime) {
        frozen = zonedDateTime;
    }

    public static void unfreeze() {
        frozen = null;
    }

}
