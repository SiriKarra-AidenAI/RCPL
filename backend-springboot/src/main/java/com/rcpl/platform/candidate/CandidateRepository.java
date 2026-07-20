package com.rcpl.platform.candidate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, String> {
    List<Candidate> findByStage(String stage);
    List<Candidate> findByCreatedBy(String createdBy);
}
