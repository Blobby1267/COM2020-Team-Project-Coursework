package com.carbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbon.model.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
