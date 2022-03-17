package app.mymemo.backend.email;

import com.sendgrid.Response;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Provides core email sending service.
 *
 * Author: Erkam Guresen
 */
//@Service
@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    @Value("${web.mail.extension}")
    private final String extension;
    @Value("${web.mail.welcome.from}")
    private final String welcomeFrom;

    // TODO normally use a queue and try until it is send

    /**
     * Sends an email to the registered email address with a token link to
     * activate the user account.
     *
     * @param toWho the email address to which the activation link will be sent.
     * @param emailBody the body of the email.
     */
    @Override
    @Async
    public void sendEmailConfirm(String toWho, String emailBody)  {
        send(toWho, "Confirm Your Email For Registration", emailBody);
    }

    /**
     * Sends an email.
     *
     * @param toWho the address of the email receiver (to).
     * @param emailSubject the subject of the email (subject).
     * @param emailBody the body of the email.
     */
    @Override
    @Async
    public void send(String toWho, String emailSubject, String emailBody) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            messageHelper.setText(emailBody, true);
            messageHelper.setTo(toWho);
            messageHelper.setSubject(emailSubject);
            messageHelper.setFrom(welcomeFrom);

        } catch (MessagingException e){
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
