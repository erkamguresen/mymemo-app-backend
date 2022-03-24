package app.mymemo.backend.email;

import com.sendgrid.Response;

/**
 * Provides core email sender interface.
 *
 * Author: Erkam Guresen
 */
public interface EmailSender {
    /**
     * Sends an email to the registered email address with a token link to
     * activate the user account.
     *
     * @param toWho the email address to which the activation link will be sent.
     * @param emailBody the body of the email.
     */
    void sendEmailConfirm(String toWho, String emailBody) ;

    /**
     * Sends an email.
     *
     * @param toWho the address of the email receiver (to).
     * @param emailSubject the subject of the email (subject).
     * @param emailBody the body of the email.
     */
    void send(String toWho, String emailSubject, String emailBody) ;
}
