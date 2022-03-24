package app.mymemo.backend.flashcard.deck;

import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.exception.UnauthorizedRequestException;
import app.mymemo.backend.flashcard.deck.model.FlashCardDeck;
import app.mymemo.backend.security.JWTTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/flash-card-deck/{userId}")
@RequiredArgsConstructor
public class FlashCardDeckController {

    private final FlashCardDeckService deckService;
    private final JWTTokenService jwtTokenService;

    @GetMapping("/{deckId}")
    public ResponseEntity<FlashCardDeck> getFlashCardDeck(
            HttpServletRequest request,
            @PathVariable String deckId,
            @PathVariable String userId){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        Optional<FlashCardDeck> cardDeckOptional = deckService.loadDeckById(deckId);

        if (cardDeckOptional.isEmpty())
            throw new BadRequestException("Flashcard deck cannot be fined.");

        FlashCardDeck cardDeck = cardDeckOptional.get();

//        if (!cardDeck.getOwnerId().equals(userId)
//                &&!cardDeck.getAllowedUserIds().contains(userId))
//            throw new UnauthorizedRequestException();

        return ResponseEntity.ok().body(cardDeck);
    }

//    @PostMapping("/{deckId}")
//    public ResponseEntity<FlashCardDeck> updateFlashCardDeck(
//            HttpServletRequest request,
//            @PathVariable String userId,
//            @PathVariable String deckId,
//            @RequestBody FlashCardDeck flashCardDeck){
//
//        String verifiedJwtUserId =
//                jwtTokenService.getAppUserIdFromHttpRequest(request);
//
//        if (!userId.equals(verifiedJwtUserId))
//            throw new UnauthorizedRequestException();
//
//        Optional<FlashCardDeck> cardDeckOptional = deckService.loadDeckById(deckId);
//
//        if (cardDeckOptional.isEmpty())
//            throw new BadRequestException("Flashcard deck cannot be fined.");
//
//        FlashCardDeck cardDeck = cardDeckOptional.get();
//
//        if (!cardDeck.getOwnerId().equals(userId)
//                &&!cardDeck.getAllowedUserIds().contains(userId))
//            throw new UnauthorizedRequestException();
//
//        return ResponseEntity.ok().body(
//                deckService.updateDeck(cardDeck, flashCardDeck));
//    }

    @PostMapping("/add")
    public ResponseEntity<FlashCardDeck> addNewFlashCardDeck(
            HttpServletRequest request,
            @PathVariable String userId,
            @RequestBody FlashCardDeck flashCardDeck){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!userId.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        if(!flashCardDeck.getOwnerId().equals(userId))
            throw new BadRequestException("Owner ids do not match in the request");

        return deckService.saveNewFlashCardDeck(flashCardDeck);
    }

//    @GetMapping("/add")
//    public ResponseEntity<FlashCardDeck> getAccessToAFlashCardDeck(
//            HttpServletRequest request,
//            @PathVariable String userId,
//            @RequestParam("token") String token
//    ){
//        String verifiedJwtUserId =
//                jwtTokenService.getAppUserIdFromHttpRequest(request);
//
//        if (!userId.equals(verifiedJwtUserId))
//            throw new UnauthorizedRequestException();
//        //TODO
//        // verify the token
//
//
//        // get the userid from token
//
//        return deckService.addUserAccessToTheDeck(userId, deckId);
//    }

//    @GetMapping("/{deckId}/share")
//    public ResponseEntity<String> createAccessTokenForAFlashCardDeck(
//            HttpServletRequest request,
//            @PathVariable String userId,
//            @PathVariable String deckId
//    ){
//
//        String verifiedJwtUserId =
//                jwtTokenService.getAppUserIdFromHttpRequest(request);
//
//        if (!userId.equals(verifiedJwtUserId))
//            throw new UnauthorizedRequestException();
//
//        Optional<FlashCardDeck> cardDeckOptional = deckService.loadDeckById(deckId);
//
//        if (cardDeckOptional.isEmpty())
//            throw new BadRequestException("Flashcard deck cannot be fined.");
//
//        FlashCardDeck cardDeck = cardDeckOptional.get();
//
//        if (!cardDeck.getOwnerId().equals(userId)
//                &&!cardDeck.getAllowedUserIds().contains(userId))
//            throw new UnauthorizedRequestException();
//
//
//
//        return ResponseEntity.ok().build("Token");
//    }

//    @PostMapping("/{deckId}/share")
//    public ResponseEntity<?> shareAccessToAFlashCardDeck(
//            HttpServletRequest request,
//            @PathVariable String userId,
//            @PathVariable String deckId,
//
//            ){
//
//        String verifiedJwtUserId =
//                jwtTokenService.getAppUserIdFromHttpRequest(request);
//
//        if (!userId.equals(verifiedJwtUserId))
//            throw new UnauthorizedRequestException();
//
//        Optional<FlashCardDeck> cardDeckOptional = deckService.loadDeckById(deckId);
//
//        if (cardDeckOptional.isEmpty())
//            throw new BadRequestException("Flashcard deck cannot be fined.");
//
//        FlashCardDeck cardDeck = cardDeckOptional.get();
//
//        if (!cardDeck.getOwnerId().equals(userId)
//                &&!cardDeck.getAllowedUserIds().contains(userId))
//            throw new UnauthorizedRequestException();
//        //TODO
//
//        return ResponseEntity.ok().build();
//    } //TODO
}
