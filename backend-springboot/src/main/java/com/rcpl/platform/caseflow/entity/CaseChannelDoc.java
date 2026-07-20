package com.rcpl.platform.caseflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The channel-gate "Updated Coverage Plan" document attached to a case. */
@Entity
@Table(name = "case_channel_docs")
@Getter
@Setter
@NoArgsConstructor
public class CaseChannelDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "file_name", length = 300)
    private String fileName;
}
