package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.UserEvent;
import io.getarrayus.securecapita.enums.EventType;
import io.getarrayus.securecapita.repository.EventRepository;
import io.getarrayus.securecapita.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
