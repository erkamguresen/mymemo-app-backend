package app.mymemo.backend.registration.token;


import app.mymemo.backend.appuser.AppUser;
import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Provides core Confirmation Token.
 * It is not specific to email conformation.
 *
 * Author: Erkam Guresen
 */
@Document
@Data
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    private String id;
    @NonNull
    private String token;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    private AppUser appUser;

    /**
     * Basic constructor for conformation
     *
     * @param token the token value of the confirmation token.
     * @param createdAt creation time stamp for the token.
     * @param expiresAt expiration time  for the token.
     * @param appUser the user for whom the token is created.
     */
    public ConfirmationToken(
            String token,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser =  appUser;
    }
}
