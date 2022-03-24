package app.mymemo.backend.flashcard.deck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Provides core user authority variables for the
 * flash card deck.
 *
 * Author: Erkam Guresen
 */
@AllArgsConstructor
@Getter
@Setter
public class UserAuthorities {
    private final String userId;
    private final String userEmail;

    private boolean canStudy;
    private boolean canShare;
    private boolean sanChange;

    public UserAuthorities(String userEmail, boolean canShare, boolean sanChange) {
        this.userId = userEmail;
        this.userEmail = userEmail;
        this.canStudy = true;
        this.canShare = canShare;
        this.sanChange = sanChange;
    }
}
