package app.mymemo.backend.registration.token;


import app.mymemo.backend.appuser.AppUser;
import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
