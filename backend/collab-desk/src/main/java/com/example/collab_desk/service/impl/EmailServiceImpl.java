package com.example.collab_desk.service.impl;

import com.example.collab_desk.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.application.name:My App}")
    private String appName;

    @Override
    public void sendInvitation(String to, String fullName, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(to);
            helper.setSubject("You're invited to " + appName + " - set your password");
            helper.setText(buildInvitationHtml(fullName, link), true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    private String buildInvitationHtml(String fullName, String link) {
        String safeName = HtmlUtils.htmlEscape(fullName);
        String safeLink = HtmlUtils.htmlEscape(link);

        return """
            <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 24px;">
              <h2 style="color: #111;">Hi %s,</h2>
              <p style="color: #444; font-size: 14px; line-height: 1.6;">
                An admin has created an account for you on <b>%s</b>.
                Click the button below to set your password and activate your account.
              </p>
              <p style="margin: 28px 0;">
                <a href="%s"
                   style="background: #000; color: #fff; padding: 12px 28px;
                          text-decoration: none; border-radius: 6px; font-size: 14px;">
                  Set Password
                </a>
              </p>
              <p style="color: #666; font-size: 12px;">
                This link is valid for 24 hours and can be used only once.
                If the button doesn't work, copy this URL into your browser:
              </p>
              <p style="color: #666; font-size: 12px; word-break: break-all;">%s</p>
              <p style="color: #999; font-size: 12px;">
                If you weren't expecting this email, you can ignore it.
              </p>
            </div>
            """.formatted(safeName, HtmlUtils.htmlEscape(appName), safeLink, safeLink);
    }
}
