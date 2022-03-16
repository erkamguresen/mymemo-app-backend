package app.mymemo.backend.email;

public interface EmailSender {
    void sendEmailConfirm(String toWho, String emailBody) ;
    void send(String toWho, String emailSubject, String emailBody) ;
}
