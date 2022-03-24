package app.mymemo.backend.user.score;

import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.user.score.model.DeckScore;
import app.mymemo.backend.user.score.model.FlashCardScore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Provides core user score service.
 *
 * Author: Erkam Guresen
 */
@Service
@RequiredArgsConstructor
public class UserScoreService {

    @NonNull
    private final UserScoreRepository userScoreRepository;

    public void updateDeckScore(DeckScore deckScore) {

        userScoreRepository.save(deckScore);
    }

    public void updateFlashCardScore(
            String userId,
            String deckId,
            String flashCardId,
            int flashCardScore) {
        DeckScore deckScore = userScoreRepository
                .findDeckScoreByUserId(userId, deckId);

        AtomicBoolean doesAnythingUpdated = new AtomicBoolean(false);

        List<FlashCardScore> flashCardScores= deckScore.getFlashCardScores()
                .stream()
                .map(flashCard -> {
                    if(flashCard.getFlashCardId().equals(flashCardId)){
                        flashCard.setFlashCardScore(flashCardScore);
                        doesAnythingUpdated.set(true);
                    }

                    return flashCard;
                })
                .collect(Collectors.toList());

        if (doesAnythingUpdated.get()){
            deckScore.setFlashCardScores(flashCardScores);

            userScoreRepository.save(deckScore);
        } else {
            throw new BadRequestException("No such card exists. " +
                    "Nothing is updated.");
        }

    }

    public DeckScore loadDeckScoreByUserId(String userId, String deckId) {
        try {
            return userScoreRepository.findDeckScoreByUserId(userId, deckId);
        } catch (Exception e){
            throw new BadRequestException(
                    "Requested flash card scores cannot be find for the user.\n"
                            + e.getMessage());
        }
    }

    public List<DeckScore> loadAllUserScoresByUserId(String userId) {
        try {
            return userScoreRepository.findAllUserScoresByUserId(userId);

        } catch (Exception e){
            throw new BadRequestException(
                    "Requested flash card scores cannot be find for the user.\n"
                            + e.getMessage());
        }
    }
}
