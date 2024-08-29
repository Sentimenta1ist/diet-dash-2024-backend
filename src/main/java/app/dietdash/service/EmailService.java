package app.dietdash.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public MimeMessage createMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    @Async
    public void send(MimeMessage email) {
        javaMailSender.send(email);
    }
}