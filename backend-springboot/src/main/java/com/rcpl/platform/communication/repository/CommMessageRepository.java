package com.rcpl.platform.communication.repository;

import java.util.List;

import com.rcpl.platform.communication.entity.CommMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommMessageRepository extends JpaRepository<CommMessage, Long> {
    List<CommMessage> findByThreadCodeOrderByCreatedAtAsc(String threadCode);
}
