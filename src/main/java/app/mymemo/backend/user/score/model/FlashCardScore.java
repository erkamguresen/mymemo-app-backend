package app.mymemo.backend.user.score.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlashCardScore {
    private String flashCardId;
    private int flashCardScore;
}
