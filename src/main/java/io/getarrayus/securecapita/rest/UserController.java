package io.getarrayus.securecapita.rest;

import io.getarrayus.securecapita.domain.HttpResponse;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.domain.UserPrincipal;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.form.LoginForm;
import io.getarrayus.securecapita.provider.TokenProvider;
import io.getarrayus.securecapita.service.RoleService;
import io.getarrayus.securecapita.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static io.getarrayus.securecapita.mapper.UserMapper.INSTANCE;
import static io.getarrayus.securecapita.utils.ExceptionUtils.processError;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDto userDto = getAuthenticatedUser(authentication);
        return userDto.isUsingMfa() ? sendVerificationCode(userDto) : sendResponse(userDto);

    }

    private UserDto getAuthenticatedUser(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDto ?
                (UserDto) authentication.getPrincipal() : ((UserPrincipal) authentication.getPrincipal()).getUser();
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
                        .data(Map.of("user", userDto))
                        .message("Profile Retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDto userDto = userService.verifyCode(email, code);
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

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
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

    private Authentication authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return authentication;
        } catch (Exception exception) {
            processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }
    }
}
