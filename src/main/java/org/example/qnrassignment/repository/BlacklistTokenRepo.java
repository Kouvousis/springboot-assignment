package org.example.qnrassignment.repository;

import org.example.qnrassignment.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepo extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
}
