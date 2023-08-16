package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.domain.UserEvent;
import io.getarrayus.securecapita.enums.EventType;

import java.util.Collection;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public interface EventService {

    Collection<UserEvent> getEventsByUserId(Long userId);

    void addUserEvent(String email, EventType eventType, String device, String ipAddress);

    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
