package app.mymemo.backend.flashcard.deck;

import app.mymemo.backend.appuser.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface FlashCardDeckRepository extends CrudRepository<AppUser, String> {
}
