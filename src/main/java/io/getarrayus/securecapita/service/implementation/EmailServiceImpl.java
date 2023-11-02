package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.enums.VerificationType;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 */

@RequiredArgsConstructor
@Service
@Slf4j
 public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender ;
 @Override
 public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {

  try{
   SimpleMailMessage mailMessage = new SimpleMailMessage();
   mailMessage.setFrom("wouwoeric@gmail.com");
   mailMessage.setTo(email);
   mailMessage.setText(getEmailMessage(firstName,verificationUrl, verificationType));
   mailMessage.setSubject(String.format("Securecapita - %s Verification Email", StringUtils.capitalize(verificationType.getType())));
   mailSender.send(mailMessage);
   log.info("Email sent to {}", firstName);
  }catch (Exception exception){
   log.error(exception.getMessage());
  }

 }

 private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {
  switch (verificationType){
   case PASSWORD -> {return  "Hello " + firstName + " \n\nReset password request. Please click the link below to reset your password. \n\n" + verificationUrl + "\n\nThe support Team "; }
   case ACCOUNT -> {return  "Hello " + firstName + " \n\nYour new account has been created. Please click the link below to verify your account. \n\n" + verificationUrl + "\n\nThe support Team "; }
   default -> {throw new ApiException("Unable to send email . Email Type unknown "); }
  }
 }
}
