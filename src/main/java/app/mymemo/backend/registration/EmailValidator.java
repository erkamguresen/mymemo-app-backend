package app.mymemo.backend.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides Email Validation Service.
 *
 * Author: Erkam Guresen
 */
@Service
public class EmailValidator implements Predicate<String> {

    private final Pattern pattern =
            Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    /**
     * Tests strings for valid email patterns.
     * @param email email to be checked.
     * @return true if the email has the email pattern.
     */
    @Override
    public boolean test(String email) {
        Matcher m = this.pattern.matcher(email);

        return m.matches();
    }
}
