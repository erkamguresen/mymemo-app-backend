package app.mymemo.backend.flashcard.deck.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class CardSide {
    private final String textInfo;

//    private final String imageUrl;
//    private final String soundUrl;
}
