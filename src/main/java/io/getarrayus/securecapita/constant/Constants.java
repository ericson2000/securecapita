package io.getarrayus.securecapita.constant;/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 */
 
 public class Constants {

  //Security
 public static final String[] PUBLIC_URLS = {"/users/login/**", "/users/verify/code/**",
         "/users/register/**", "/users/error/**", "/users/resetpassword/**",
         "/users/verify/password/**", "/users/verify/account/**", "/users/refresh/token/**", "/users/image/**", "/users/new/password/**"};

 public static final String TOKEN_PREFIX = "Bearer ";
 public static final String[] PUBLIC_ROUTES = {"/users/login", "/users/verify/code", "/users/register", "/users/refresh/token", "/users/image" , "/users/new/password"};
 public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

 public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
 public static final String GET_ARRAYS_LLC = "GET_ARRAYS_LLC";
 public static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
 public static final String AUTHORITIES = "authorities";
 public static final long ACCESS_TOKEN_EXPIRATION_TIME = 432_000_000;
 public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

 //DATE
 public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

}
