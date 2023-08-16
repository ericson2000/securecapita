package io.getarrayus.securecapita.rowmapper;

import io.getarrayus.securecapita.domain.UserEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class UserEventRowMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return UserEvent.builder()
                .id(resultSet.getLong("id"))
                .type(resultSet.getString("type"))
                .description(resultSet.getString("description"))
                .device(resultSet.getString("device"))
                .ipAddress(resultSet.getString("ip_address"))
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();

    }
}
