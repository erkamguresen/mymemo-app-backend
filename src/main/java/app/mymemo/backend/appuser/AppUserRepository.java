package app.mymemo.backend.appuser;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, String> {

    /**
     * Retrieves an entity by its username.
     * @param username must not be null.
     * @return an Optional describing the AppUser with the given username,
     * or an empty Optional if the username is not found in the database
     */
    @Query("{ 'username' : ?0 }")
    Optional<AppUser> findUserByUsername(String username);

    /**
     * Retrieves an entity by its email.
     * @param email must not be null.
     * @return an Optional describing the AppUser with the given username,
     * or an empty Optional if the username is not found in the database
     */
    @Query("{ 'email' : ?0 }")
    Optional<AppUser> findUserByEmail(String email);

}