package io.getarrayus.securecapita.repository.implementation;

import io.getarrayus.securecapita.domain.UserEvent;
import io.getarrayus.securecapita.enums.EventType;
import io.getarrayus.securecapita.repository.EventRepository;
import io.getarrayus.securecapita.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static io.getarrayus.securecapita.query.EventQuery.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static io.getarrayus.securecapita.query.EventQuery.SELECT_EVENTS_BY_USER_ID_QUERY;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@RequiredArgsConstructor
@Repository
@Slf4j
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, Map.of("userId", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, Map.of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
