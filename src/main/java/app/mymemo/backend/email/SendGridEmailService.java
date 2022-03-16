package app.mymemo.backend.email;

import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService implements EmailSender{

    private final Environment environment;
    private final String SENDGRID_API_KEY;
    private final String webMailSender;

    @Autowired
    public SendGridEmailService(Environment environment) {
        this.environment = environment;
        this.SENDGRID_API_KEY = this.environment.getProperty("SENDGRID_API_KEY");
        this.webMailSender = this.environment.getProperty("web.mail.welcome-from");
    }

    @Override
    public void sendEmailConfirm(String toWho, String emailBody) {
        send(
                toWho,
                "Confirm Your Email",
                emailBody
        );
    }

    @Override
    public void send(String toWho, String emailSubject, String emailBody) {

        Email from = new Email(webMailSender);
        String subject = emailSubject;
        Email to = new Email(toWho);
        Content content = new Content("text/html", emailBody);
        Mail mail = new Mail(from, subject, to, content);


        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to send email");
        }
    }
}