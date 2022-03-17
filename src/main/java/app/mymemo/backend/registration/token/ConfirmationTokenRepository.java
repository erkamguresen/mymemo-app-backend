package app.mymemo.backend.registration.token;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Provides core Confirmation Token Repository.
 *
 * Author: Erkam Guresen
 */
public interface ConfirmationTokenRepository
        extends CrudRepository<ConfirmationToken, String> {

    /**
     * Finds confirmation the token based on its token value.
     *
     * @param token token value to be searched.
     * @return an optional with a confirmation token if the token value is
     * found in the database, null otherwise.
     */
    @Query("{ 'token' : ?0 }")
    Optional<ConfirmationToken> findByToken(String token);
}
