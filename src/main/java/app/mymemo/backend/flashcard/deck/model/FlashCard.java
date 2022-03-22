package app.mymemo.backend.flashcard.deck.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlashCard {
    private final FrontSide frontSide;
    private final BackSide backSide;
    private final OtherQuizChoices otherQuizChoices;

}
