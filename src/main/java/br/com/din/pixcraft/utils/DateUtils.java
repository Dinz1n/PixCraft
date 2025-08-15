package br.com.din.pixcraft.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    public static String toMpExpirationOrDefault(String durationStr, Duration defaultIfInvalid) {
        Duration duration;
        try {
            Pattern pattern = Pattern.compile("(?i)^(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$");
            Matcher matcher = pattern.matcher(durationStr == null ? "" : durationStr.trim());
            if (!matcher.matches()) throw new IllegalArgumentException("Formato inválido");

            long days = matcher.group(1) != null ? Long.parseLong(matcher.group(1)) : 0;
            long hours = matcher.group(2) != null ? Long.parseLong(matcher.group(2)) : 0;
            long minutes = matcher.group(3) != null ? Long.parseLong(matcher.group(3)) : 0;
            long seconds = matcher.group(4) != null ? Long.parseLong(matcher.group(4)) : 0;

            duration = Duration.ofDays(days)
                    .plusHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds);
        } catch (Exception e) {
            System.err.println("[Aviso] entrada inválida para expiração ('" + durationStr + "'): " +
                    e.getMessage() + ". Usando padrão de " + defaultIfInvalid.toMinutes() + " minutos.");
            duration = defaultIfInvalid;
        }

        Instant expirationInstant = Instant.now().plus(duration);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .withZone(ZoneOffset.UTC)
                .format(expirationInstant.truncatedTo(ChronoUnit.SECONDS));
    }
}