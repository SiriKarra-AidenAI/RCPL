package com.rcpl.platform.communication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A case/partner communication thread (Thread in the contract). */
@Entity
@Table(name = "comm_threads")
@Getter
@Setter
@NoArgsConstructor
public class CommThread {

    @Id
    @Column(length = 64)
    private String code;

    @Column(length = 128)
    private String town;

    @Column(name = "partner_name", length = 300)
    private String partnerName;

    @Column(length = 24)
    private String audience; // internal | partner

    @Column(name = "last_message", length = 2000)
    private String lastMessage;
}
