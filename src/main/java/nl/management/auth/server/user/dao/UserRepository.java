package nl.management.auth.server.user.dao;

import nl.management.auth.server.user.models.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface UserRepository<T extends User, E> extends CrudRepository<T, E> {
    T findByUuid(UUID uuid);

    T findByUuidAndActiveFalse(UUID uuid);

    T findByUuidAndActiveTrue(UUID uuid);

    T findByUsername(String username);
}
