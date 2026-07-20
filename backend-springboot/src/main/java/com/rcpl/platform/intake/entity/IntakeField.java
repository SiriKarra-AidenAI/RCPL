package com.rcpl.platform.intake.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One extracted label/value field of an intake item. */
@Entity
@Table(name = "intake_fields")
@Getter
@Setter
@NoArgsConstructor
public class IntakeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intake_id", nullable = false, length = 64)
    private String intakeId;

    @Column(length = 200)
    private String label;

    @Column(length = 1000)
    private String value;

    @Column
    private boolean ok = true;
}
