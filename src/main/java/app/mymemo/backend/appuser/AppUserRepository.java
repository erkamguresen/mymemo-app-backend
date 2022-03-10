package app.mymemo.backend.appuser;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Provides core user interface for database connection.
 *
 * Author: Erkam Guresen
 */
public interface AppUserRepository extends CrudRepository<AppUser, String> {

    /**
     * Retrieves an entity by its username.
     * @param username must not be null.
     * @return an AppUser with the given username.
     */
//    @Query("{ 'username' : ?0 }") //?0 is the input index, it is here the username
//    AppUser findUserByUsername(String username);

    /**
     * Retrieves an entity by its email.
     * @param email must not be null.
     * @return an  AppUser with the given username.

     */
    @Query("{ 'email' : ?0 }") //?0 is the input index, it is here the email
    AppUser findUserByEmail(String email);

    /**
     * Retrieves an entity by its id.
     * @param id must not be null.
     * @return an  AppUser with the given id.

     */
    @Query("{ 'id' : ?0 }") //?0 is the input index, it is here the email
    AppUser findUserById(String id);
}