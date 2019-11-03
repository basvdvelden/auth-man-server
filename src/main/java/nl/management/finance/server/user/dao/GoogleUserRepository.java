package nl.management.finance.server.user.dao;

import nl.management.finance.server.user.models.GoogleUser;
import nl.management.finance.server.user.models.NativeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleUserRepository extends JpaRepository<GoogleUser, String> {

    GoogleUser findByUsername(String username);
}
