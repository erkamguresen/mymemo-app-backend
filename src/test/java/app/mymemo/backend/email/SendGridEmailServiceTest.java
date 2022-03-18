package app.mymemo.backend.email;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.registration.token.ConfirmationTokenService;
import com.sendgrid.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SendGridEmailServiceTest {

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @Test
    void itCanSendEmailConfirm() {
        //given

        //when
        sendGridEmailService.sendEmailConfirm(
                "admin@mymemo.app",
                "Hello Jhon"
        );

        //then
        /*It should not throw IllegalStateException*/

    }

    @Test
    void send() {
        //given

        //when
        sendGridEmailService.send(
                "admin@mymemo.app",
                "email sbuject",
                "Hello Jhon"
        );

        //then
        /*It should not throw IllegalStateException*/
    }
}