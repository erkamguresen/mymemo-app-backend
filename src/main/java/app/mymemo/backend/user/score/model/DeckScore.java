package app.mymemo.backend.user.score.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DeckScore {
    private String deckId;
    private List<FlashCardScore> flashCardScores = new ArrayList<>();

    // TODO do we need @Setter or constructor
}
