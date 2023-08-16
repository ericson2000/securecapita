package io.getarrayus.securecapita.listener;

import io.getarrayus.securecapita.event.NewUserEvent;
import io.getarrayus.securecapita.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.getarrayus.securecapita.utils.RequestUtils.getDevice;
import static io.getarrayus.securecapita.utils.RequestUtils.getIpAddress;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        eventService.addUserEvent(event.getEmail(), event.getType(), getDevice(request), getIpAddress(request));
    }
}
