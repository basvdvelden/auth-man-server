package nl.management.auth.server.user.dao;

import nl.management.auth.server.user.models.entities.GoogleUser;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GoogleUserRepository extends UserRepository<GoogleUser, UUID> {
}
