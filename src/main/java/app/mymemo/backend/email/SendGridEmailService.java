package app.mymemo.backend.email;

import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Provides core email sending service.
 *
 * Author: Erkam Guresen
 */
@Service
public class SendGridEmailService implements EmailSender{

    private final Environment environment;
    private final String SENDGRID_API_KEY;
    private final String webMailSender;

    /**
     * Basic email sending service constructor.
     *
     * @param environment environment of the app.
     */
    @Autowired
    public SendGridEmailService(Environment environment) {
        this.environment = environment;
        this.SENDGRID_API_KEY = this.environment.getProperty("SENDGRID_API_KEY");
        this.webMailSender = this.environment.getProperty("web.mail.welcome-from");
    }

    /**
     * Sends an email to the registered email address with a token link to
     * activate the user account.
     *
     * @param toWho the email address to which the activation link will be sent.
     * @param emailBody the body of the email.
     */
    @Override
    public void sendEmailConfirm(String toWho, String emailBody) {
        send(
                toWho,
                "Confirm Your Email For MyMemo .App Registration",
                emailBody
        );
    }

    /**
     * Sends an email.
     *  @param toWho the address of the email receiver (to).
     * @param emailSubject the subject of the email (subject).
     * @param emailBody the body of the email.
     * @return a sendgrid api respond object.
     */
    @Override
    public void send(String toWho, String emailSubject, String emailBody) {

        Email from = new Email(webMailSender);
        String subject = emailSubject;
        Email to = new Email(toWho);
        Content content = new Content("text/html", emailBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        Response response;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sg.api(request);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to send email");
        }
    }
}