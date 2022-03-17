package app.mymemo.backend.registration;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.email.EmailSender;
import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.registration.email.template.RegistrationConfirmEmail;
import app.mymemo.backend.registration.token.ConfirmationToken;
import app.mymemo.backend.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides app user registration service.
 *
 * Author: Erkam Guresen
 */
@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final Environment environment;


    /**
     * Registers a new user with a default role as "APP_USER_ROLE".
     * @param request registration request from client.
     * @return a confirmation token of registration.
     */
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail){
            throw new BadRequestException("email is not valid");
        }

        // add default user role to new registration
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);

        String token =appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        roles
                )
        );

        emailSender.sendEmailConfirm(
                request.getEmail(),
                RegistrationConfirmEmail.buildRegistrationConfirmEmail(request.getFirstName(),
                        environment.getProperty("web.mail.confirm-link") + token)
                );

        return token;
    }

    /**
     * Confirms that an app user has access to the registered email account.
     * @param token unique token to confirm the email address.
     * @return "confirmed" if token is valid.
     * @throws BadRequestException
     */
    @Transactional
    public String confirmToken(String token) throws BadRequestException{

        Optional<ConfirmationToken> confirmationTokenOptional =
                confirmationTokenService.getToken(token);

        if (confirmationTokenOptional.isEmpty()) {
            throw new BadRequestException("Token not found");
        }

        ConfirmationToken confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getConfirmedAt() != null) {
            throw new BadRequestException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token expired");
        }


        appUserService.enableAppUser(
                confirmationToken.getAppUser());

        confirmationTokenService.setConfirmedAt(confirmationToken);

        return "confirmed";

    }


}
