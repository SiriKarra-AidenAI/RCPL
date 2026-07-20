package com.rcpl.platform.grievance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dated update on a grievance's timeline. */
@Entity
@Table(name = "grievance_updates")
@Getter
@Setter
@NoArgsConstructor
public class GrievanceUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grievance_id", nullable = false, length = 64)
    private String grievanceId;

    @Column(name = "on_date", length = 32)
    private String onDate;

    @Column(name = "by_actor", length = 200)
    private String byActor;

    @Column(length = 2000)
    private String note;
}
