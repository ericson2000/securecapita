package io.getarrayus.securecapita.query;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class EventQuery {

    public static final String SELECT_EVENTS_BY_USER_ID_QUERY = "SELECT uev.id, uev.device, uev.ip_address, ev.type, ev.description, uev.created_at FROM UserEvents uev JOIN Events ev ON uev.event_id = ev.id JOIN Users u ON u.id = uev.user_id WHERE u.id = :userId ORDER BY uev.created_at DESC LIMIT 10";
    public static final String INSERT_EVENT_BY_USER_EMAIL_QUERY = "INSERT INTO UserEvents (user_id, event_id, device, ip_address) VALUES ((SELECT id FROM Users WHERE email = :email), (SELECT id FROM Events WHERE type = :type), :device, :ipAddress)";
}
