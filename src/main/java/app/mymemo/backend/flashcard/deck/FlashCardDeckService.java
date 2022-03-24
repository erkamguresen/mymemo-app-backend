package app.mymemo.backend.flashcard.deck;

import app.mymemo.backend.flashcard.deck.model.FlashCardDeck;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Provides core flashcard deck service.
 *
 * Author: Erkam Guresen
 */
@Service
@RequiredArgsConstructor
public class FlashCardDeckService {

    @NonNull
    private FlashCardDeckRepository cardDeckRepository;

    public Optional<FlashCardDeck> loadDeckById(String deckId) {

        return cardDeckRepository.findById(deckId);
    }

    public FlashCardDeck updateDeck(FlashCardDeck cardDeck, FlashCardDeck flashCardDeck) {
        // TODO select fields to do not update all of them
        return null;
    }

    public ResponseEntity<FlashCardDeck> saveNewFlashCardDeck(
            FlashCardDeck flashCardDeck) {

        //TODO save a new card deck
        return null;
    }
}
