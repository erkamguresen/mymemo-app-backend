package app.mymemo.backend.user.score;

import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.exception.UnauthorizedRequestException;
import app.mymemo.backend.security.JWTTokenService;
import app.mymemo.backend.user.score.model.DeckScore;
import app.mymemo.backend.user.score.model.FlashCardScore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-progress/{userId}/stats")
@RequiredArgsConstructor
public class UserScoreController {
    private final UserScoreService userScoreService;
    private final JWTTokenService jwtTokenService;

    @GetMapping
    public ResponseEntity<List<DeckScore>> getUserScores(
            HttpServletRequest request,
            @PathVariable String userId){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        return ResponseEntity.ok().body(
                userScoreService.loadAllUserScoresByUserId(userId));
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<DeckScore> getDeckScores(
            HttpServletRequest request,
            @PathVariable String deckId,
            @PathVariable String userId){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        return ResponseEntity.ok().body(
                userScoreService.loadDeckScoreByUserId(userId, deckId));
    }

    @PostMapping("/{deckId}")
    public ResponseEntity<?> updateDeckScores(
            HttpServletRequest request,
            @PathVariable String deckId,
            @PathVariable String userId,
            @RequestBody DeckScore deckScore){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        if(!deckScore.getUserId().equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        if(!deckScore.getDeckId().equals(deckId))
            throw new BadRequestException(
                    "Deck ids in url and request body are different.");

        userScoreService.updateDeckScore(deckScore);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deckId}/{flashCardId}")
    public ResponseEntity<?> updateFlashCardScore(
            HttpServletRequest request,
            @PathVariable String deckId,
            @PathVariable String userId,
            @PathVariable String flashCardId,
            @RequestBody FlashCardScore flashCardScore){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        //check if updating the correct flashcard
        if (!flashCardScore.getFlashCardId().equals(flashCardId))
            throw new BadRequestException("Trying to update wrong card.");

        // check if updating own score
        DeckScore deckScore =
                userScoreService.loadDeckScoreByUserId(userId,deckId);

        if(!deckScore.getUserId().equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        userScoreService.updateFlashCardScore(
                userId,
                deckId,
                flashCardId,
                flashCardScore.getFlashCardScore());

        return ResponseEntity.ok().build();
    }
}
