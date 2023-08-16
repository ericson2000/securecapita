package io.getarrayus.securecapita.query;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class CustomerQuery {

    public static final String CUSTOMER_STATS_QUERY = "SELECT c.total_customers, i.total_invoices, inv.total_billed FROM (SELECT COUNT(*) total_customers FROM Customer) c, (SELECT COUNT(*) total_invoices FROM Invoice) i, (SELECT ROUND(SUM(total)) total_billed FROM Invoice) inv";
}
