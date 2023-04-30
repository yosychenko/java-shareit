package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Slf4j
public class ShareItServer {
    @PostConstruct
    public void startupApplication() {
        log.info("Starting application with JAVA_OPTS: " + System.getenv("JAVA_OPTS"));
    }

    public static void main(String[] args) {

        SpringApplication.run(ShareItServer.class, args);
    }

}