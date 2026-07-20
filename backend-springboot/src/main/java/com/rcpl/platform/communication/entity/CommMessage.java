package com.rcpl.platform.communication.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A message within a communication thread (CaseMessage in the contract). */
@Entity
@Table(name = "comm_messages")
@Getter
@Setter
@NoArgsConstructor
public class CommMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_code", nullable = false, length = 64)
    private String threadCode;

    @Column(name = "author_role", length = 32)
    private String authorRole;

    @Column(name = "author_name", length = 200)
    private String authorName;

    @Column(length = 2000)
    private String body;

    @Column(name = "is_next_replier")
    private boolean nextReplier;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
