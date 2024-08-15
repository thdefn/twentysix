package cm.twentysix.order.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdUtil {
    public static String generate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "-" +
                UUID.randomUUID().toString().replaceAll("-", "");
    }
}
