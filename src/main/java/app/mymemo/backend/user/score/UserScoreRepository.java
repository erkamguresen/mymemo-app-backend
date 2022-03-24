package app.mymemo.backend.user.score;

import app.mymemo.backend.user.score.model.DeckScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Provides core user score interface for database connection.
 *
 * Author: Erkam Guresen
 */
public interface UserScoreRepository extends MongoRepository<DeckScore, String> {

    @Query("{ 'userId' : ?0, 'deckId' : ?1 }")
    DeckScore findDeckScoreByUserId(String userId, String deckId);

    @Query("{'userId' : ?0}")
    List<DeckScore> findAllUserScoresByUserId(String userId);


}
