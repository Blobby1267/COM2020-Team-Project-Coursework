package com.carbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByStatus(EvidenceStatus status);
}
