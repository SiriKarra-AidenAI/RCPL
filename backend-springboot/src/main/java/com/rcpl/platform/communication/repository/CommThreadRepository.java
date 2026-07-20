package com.rcpl.platform.communication.repository;

import com.rcpl.platform.communication.entity.CommThread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommThreadRepository extends JpaRepository<CommThread, String> {
}
