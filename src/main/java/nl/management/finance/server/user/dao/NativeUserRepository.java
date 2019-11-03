package nl.management.finance.server.user.dao;

import nl.management.finance.server.user.models.NativeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NativeUserRepository extends JpaRepository<NativeUser, String> {

    NativeUser findByUsername(String username);
}
