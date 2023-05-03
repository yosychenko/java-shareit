package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupLogger {
    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("Starting application with JAVA_OPTS: " + System.getenv("JAVA_OPTS"));
    }
}
