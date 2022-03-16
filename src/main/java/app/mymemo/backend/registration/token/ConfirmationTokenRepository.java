package app.mymemo.backend.registration.token;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository
        extends CrudRepository<ConfirmationToken, String> {

    @Query("{ 'token' : ?0 }")
    Optional<ConfirmationToken> findByToken(String token);
}
