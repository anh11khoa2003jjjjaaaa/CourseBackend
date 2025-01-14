package org.example.sellingcourese.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // true indicates HTML content

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        String subject = "Welcome to Our Course Platform!";
        String content = String.format("""
            <html>
                <body>
                    <h2>Welcome %s!</h2>
                    <p>Thank you for joining our course platform. We're excited to have you here!</p>
                    <p>Start exploring our courses and begin your learning journey today.</p>
                    <br>
                    <p>Best regards,</p>
                    <p>Course Platform Team</p>
                </body>
            </html>
            """, name);

        sendEmail(to, subject, content);
    }
}