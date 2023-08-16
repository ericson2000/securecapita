package io.getarrayus.securecapita.rest;

import io.getarrayus.securecapita.domain.HttpResponse;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.domain.UserPrincipal;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.event.NewUserEvent;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.form.LoginForm;
import io.getarrayus.securecapita.form.SettingsForm;
import io.getarrayus.securecapita.form.UpdateForm;
import io.getarrayus.securecapita.form.UpdatePasswordForm;
import io.getarrayus.securecapita.provider.TokenProvider;
import io.getarrayus.securecapita.service.EventService;
import io.getarrayus.securecapita.service.RoleService;
import io.getarrayus.securecapita.service.UserService;
import io.getarrayus.securecapita.utils.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static io.getarrayus.securecapita.enums.EventType.*;
import static io.getarrayus.securecapita.mapper.UserMapper.INSTANCE;
import static io.getarrayus.securecapita.utils.UserUtils.getAuthenticatedUser;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private static final String TOKEN_PREFIX = "Bearer ";

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher;
    private final EventService eventService;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        UserDto userDto = authenticate(loginForm.getEmail(), loginForm.getPassword());
        return userDto.isUsingMfa() ? sendVerificationCode(userDto) : sendResponse(userDto);

    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDto userDto = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto))
                        .message("user created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        UserDto userDto = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto, "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Profile Retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) {
        UserDto updateUserDto = userService.updateUserDetails(user);
        publisher.publishEvent(new NewUserEvent(updateUserDto.getEmail(), PROFILE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", updateUserDto, "events", eventService.getEventsByUserId(updateUserDto.getId()), "roles", roleService.getRoles()))
                        .message("User Updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDto userDto = userService.verifyCode(email, code);
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), LOGIN_ATTEMPT_SUCCESS));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto, "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDto)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDto))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    // START- To reset password when user is not logged in

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Email sent. Please check your email to reset your password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDto userDto = getAuthenticatedUser(authentication);
        userService.updatePassword(userDto.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), PASSWORD_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserById(userDto.getId()), "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Password Updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateRole(Authentication authentication, @PathVariable("roleName") String roleName) {
        UserDto userDto = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDto.getId(), roleName);
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), ROLE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserById(userDto.getId()), "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Password Updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid SettingsForm form) {
        UserDto userDto = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDto.getId(), form.getEnabled(), form.getLocked());
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), ACCOUNT_SETTINGS_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserById(userDto.getId()), "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Account Setting Updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) {
        UserDto userDto = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), MFA_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserById(userDto.getId()), "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Multi-Factor Authentication updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image") MultipartFile image) {
        UserDto userDto = getAuthenticatedUser(authentication);
        publisher.publishEvent(new NewUserEvent(userDto.getEmail(), PROFILE_PICTURE_UPDATE));
        userService.updateImage(userDto, image);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserById(userDto.getId()), "events", eventService.getEventsByUserId(userDto.getId()), "roles", roleService.getRoles()))
                        .message("Profile Image updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(value = "/image/{imageName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("imageName") String imageName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + imageName));
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key) {
        UserDto userDto = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto))
                        .message("Please enter a new password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> renewPasswordWithKey(@PathVariable("key") String key, @PathVariable("password") String password,
                                                             @PathVariable("confirmPassword") String confirmPassword) {

        userService.renewPassword(key, password, confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Password rest successfully.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    // END- To reset password when user is not logged in


    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message(userService.verifyAccountKey(key).isEnabled() ? "Account already verified " : "Account verified")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if (isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDto userDto = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("user", userDto, "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDto)),
                                    "refresh_token", token))
                            .message("Token refreshed")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        }
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Refresh Token missing or invalid")
                        .developerMessage("Refresh Token missing or invalid")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(
                tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }

//    @RequestMapping("/error")
//    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
//        return ResponseEntity.badRequest().body(
//                HttpResponse.builder()
//                        .timeStamp(LocalDateTime.now().toString())
//                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
//                        .status(BAD_REQUEST)
//                        .statusCode(BAD_REQUEST.value())
//                        .build());
//    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .build(), NOT_FOUND);
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDto userDto) {
        userService.sendVerificationCode(userDto);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto))
                        .message("Verification Code Sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDto userDto) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDto, "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDto)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDto))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDto userDto) {
        return new UserPrincipal(INSTANCE.userDtoToUser(userService.getUserByEmail(userDto.getEmail())), roleService.getRoleByUserId(userDto.getId()));
    }

    private UserDto authenticate(String email, String password) {
        try {
            if (Objects.nonNull(userService.getUserByEmail(email))) {
                publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT));
            }
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDto loggedInUser = getAuthenticatedUser(authentication);
            if (!loggedInUser.isUsingMfa()) {
                publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_SUCCESS));
            }
            return getAuthenticatedUser(authentication);
        } catch (Exception exception) {
            publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_FAILURE));
            ExceptionUtils.processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }
    }
}
