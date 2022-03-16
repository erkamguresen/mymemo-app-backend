package app.mymemo.backend.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

    @Override
    @Async
    public void sendEmailConfirm(String toWho, String emailBody)  {
        send(toWho, "Confirm Your Email", emailBody);
    }

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
