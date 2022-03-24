package app.mymemo.backend.flashcard.deck.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides core flashcard deck information.
 *
 * Author: Erkam Guresen
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FlashCardDeck {
    @Id
    private String id;
    private String ownerId;
    private String ownerEmail;

    private String title;
    private String subTitle;

    private List<FlashCard> flashCards = new ArrayList<FlashCard>();

    private List<UserAuthorities> userAuthorities = new ArrayList<UserAuthorities>();
}
