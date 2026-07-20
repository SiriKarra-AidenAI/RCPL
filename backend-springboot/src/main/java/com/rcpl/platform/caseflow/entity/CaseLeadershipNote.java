package com.rcpl.platform.caseflow.entity;

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

/** A note left for Leadership on a case, readable once it reaches their queue. */
@Entity
@Table(name = "case_notes_for_leadership")
@Getter
@Setter
@NoArgsConstructor
public class CaseLeadershipNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(length = 200)
    private String author;

    @Column(length = 2000)
    private String body;

    @Column(name = "when_label", length = 32)
    private String whenLabel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
