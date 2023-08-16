package io.getarrayus.securecapita.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static nl.basjes.parse.useragent.UserAgent.AGENT_NAME;
import static nl.basjes.parse.useragent.UserAgent.DEVICE_NAME;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class RequestUtils {

    public static final String USER_AGENT_HEADER = "user-agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = "Unknow IP";
        if (Objects.nonNull(request)) {
            ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
            if (Objects.isNull(ipAddress) || ipAddress.equals(StringUtils.EMPTY)) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
        UserAgent userAgent = userAgentAnalyzer.parse(request.getHeader(USER_AGENT_HEADER));
        return userAgent.getValue(AGENT_NAME) + " - " + userAgent.getValue(DEVICE_NAME);
//        return userAgent.getValue(OPERATING_SYSTEM_NAME) + " - " + userAgent.getValue(AGENT_NAME) + " - " + userAgent.getValue(DEVICE_NAME);
    }
}
