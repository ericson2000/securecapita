package io.getarrayus.securecapita.rowmapper;

import io.getarrayus.securecapita.domain.Stats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class StatsRowMapper implements RowMapper<Stats> {

    @Override
    public Stats mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(resultSet.getInt("total_customers"))
                .totalInvoices(resultSet.getInt("total_invoices"))
                .totalBilled(resultSet.getDouble("total_billed"))
                .build();
    }
}
