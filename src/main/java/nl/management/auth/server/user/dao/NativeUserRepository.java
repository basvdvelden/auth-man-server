package nl.management.auth.server.user.dao;

import nl.management.auth.server.user.models.entities.NativeUser;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NativeUserRepository extends UserRepository<NativeUser, UUID> {
}
