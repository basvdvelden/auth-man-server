package nl.management.finance.server.common.jwt;

import nl.management.finance.server.common.jwt.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserUuid(UUID userUuid);
}
