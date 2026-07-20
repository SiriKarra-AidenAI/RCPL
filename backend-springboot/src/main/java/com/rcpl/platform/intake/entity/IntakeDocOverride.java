package com.rcpl.platform.intake.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A replaced/uploaded document for an intake item (Intake Review "replace document"). */
@Entity
@Table(name = "intake_doc_overrides")
@Getter
@Setter
@NoArgsConstructor
public class IntakeDocOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intake_id", nullable = false, length = 64)
    private String intakeId;

    @Column(name = "doc_name", nullable = false, length = 200)
    private String docName;

    @Column(name = "file_name", length = 300)
    private String fileName;

    @Lob
    @Column(name = "data_url")
    private String dataUrl;
}
