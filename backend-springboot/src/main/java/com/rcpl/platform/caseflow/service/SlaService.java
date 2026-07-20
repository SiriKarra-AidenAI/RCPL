package com.rcpl.platform.caseflow.service;

import org.springframework.stereotype.Service;

/** SLA label formatting, mirroring slaLabelFromHours() in the frontend store. */
@Service
public class SlaService {

    /** e.g. 6 -> "6h left", 48 -> "2d left". Non-positive -> "Overdue". */
    public String label(int hours) {
        if (hours <= 0) return "Overdue";
        return hours % 24 == 0 ? (hours / 24) + "d left" : hours + "h left";
    }
}
