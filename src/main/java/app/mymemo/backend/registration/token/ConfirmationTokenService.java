package app.mymemo.backend.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Provides Confirmation Token Service.
 *
 * Author: Erkam Guresen
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    /**
     * Saves an email confirmation token to database.
     *
     * @param confirmationToken email confirmation token to be saved.
     */
    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        repository.save(confirmationToken);
    }

    /**
     * Finds the email confirmation token in the database.
     *
     * @param token email token to be searched in database.
     * @return an optional with an email confirmation token if the given email token
     * exists in the database otherwise null.
     */
    public Optional<ConfirmationToken> getToken(String token) {

        return repository.findByToken(token);
    }

    /**
     * Change the status of the confirmation token by adding a confirmation time to the
     * confirmation token.
     *
     * @param confirmationToken Confirmation token in which confirmation time will be added.
     * @return saved Confirmation token in database with confirmation time.
     */
    public ConfirmationToken setConfirmedAt(ConfirmationToken confirmationToken) {

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        return repository.save(confirmationToken);
    }
}
