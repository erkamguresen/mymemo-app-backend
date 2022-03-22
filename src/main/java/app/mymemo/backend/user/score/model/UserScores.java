package app.mymemo.backend.user.score.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides core user score information.
 *
 * Author: Erkam Guresen
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Document
public class UserScores {
    @Id
    private String id;
    private String userId;
    private String userEmail;
    private List<DeckScore> deckScores = new ArrayList<>();



}
