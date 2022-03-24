package app.mymemo.backend.flashcard.deck;

import app.mymemo.backend.flashcard.deck.model.FlashCardDeck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Provides core flash card deck interface for database connection.
 *
 * Author: Erkam Guresen
 */
public interface FlashCardDeckRepository extends MongoRepository<FlashCardDeck, String> {

    //TODO findByOwnerId;

    //TODO findByOwnerEmail;
//    @Query(value="{'userId' : ?0}", fields="{'deckOwnerId':0}")
}
