package app.mymemo.backend.flashcard.deck.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Document
public class FlashCardDeck {
    @Id
    private String id;
    private List<FlashCard> flashCards = new ArrayList<FlashCard>();

}
