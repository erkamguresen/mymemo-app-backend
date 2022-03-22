package app.mymemo.backend.user.score.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class FlashCardScore {
    private String flashCardId;
    private int flashCardScore;

    // TODO write score update methods instead of setters?
}
