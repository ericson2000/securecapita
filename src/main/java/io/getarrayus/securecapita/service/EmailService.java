package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.enums.VerificationType;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 */
 
 public interface EmailService {

  void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);
}
