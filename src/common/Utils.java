package common;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class Utils {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static Consumer<String> logConsumer;

    public static void setLogConsumer(Consumer<String> consumer) {
        logConsumer = consumer;
    }

    public static void log(String message) {
        String formatted = "[" + LocalTime.now().format(FORMAT) + "] " + message;

        if (logConsumer != null) {
            SwingUtilities.invokeLater(() -> logConsumer.accept(formatted));
        } else {
            System.out.println(formatted);
        }
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}