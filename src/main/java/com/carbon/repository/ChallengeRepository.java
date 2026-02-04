package com.carbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbon.model.Challenge;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByFrequency(String frequency);
}
