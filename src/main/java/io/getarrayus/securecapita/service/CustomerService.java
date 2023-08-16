package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.domain.Customer;
import io.getarrayus.securecapita.domain.Invoice;
import io.getarrayus.securecapita.domain.Stats;
import org.springframework.data.domain.Page;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public interface CustomerService {

    //Customer functions

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    Page<Customer> getCustomers(int page, int size);

    Iterable<Customer> getCustomers();

    Customer getCustomer(Long id);

    Page<Customer> searchCustomer(String name, int page, int size);

    //Invoice functions

    Invoice createInvoice(Invoice invoice);

    Page<Invoice> getInvoice(int page, int size);

    void addInvoiceToCustomer(Long customerId, Invoice invoice);

    Invoice getInvoice(Long invoiceId);

    Stats getStats();
}
