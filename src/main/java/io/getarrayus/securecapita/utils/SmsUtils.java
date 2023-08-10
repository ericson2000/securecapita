package io.getarrayus.securecapita.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class SmsUtils {
    private static final String FROM_NUMBER = "+14782105287";
    private static final String SID_KEY = "AC5f1f44ea48833e4cd5b0275b54130a48";
    private static final String TOKEN_KEY = "bf9641eb42186d0dbbce856c1e3f8775";

    public static void sendSMS(String phoneNumber, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = Message.creator(new PhoneNumber("+230" + phoneNumber), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println("Message : " + message);
    }

}
