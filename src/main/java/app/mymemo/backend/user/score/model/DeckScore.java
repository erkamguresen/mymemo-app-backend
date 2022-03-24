package app.mymemo.backend.user.score.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides basic user score information
 * on a specific flash card deck.
 *
 * Author: Erkam Guresen
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DeckScore {
    @Id
    private String id;
    private String userId;
    private String userEmail;
    private String deckId;
    private String deckOwnerId;
    private List<FlashCardScore> flashCardScores = new ArrayList<>();
//    private HashMap<String, Integer> flashCardScores = new HashMap<>();

}
