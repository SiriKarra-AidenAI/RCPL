package com.rcpl.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * RCPL Partner Platform — legacy (non-agentic) Spring Boot backend.
 *
 * <p>System of record over Oracle for the Partner Platform. No LLMs, no agents,
 * no copilot: all scoring/routing/gating is deterministic Java and every
 * state-changing action writes an audit row.
 */
@SpringBootApplication
@EnableScheduling
public class RcplApplication {

    public static void main(String[] args) {
        SpringApplication.run(RcplApplication.class, args);
    }
}
