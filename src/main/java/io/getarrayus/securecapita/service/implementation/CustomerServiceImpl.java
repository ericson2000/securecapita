package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.Customer;
import io.getarrayus.securecapita.domain.Invoice;
import io.getarrayus.securecapita.domain.Stats;
import io.getarrayus.securecapita.repository.CustomerRepository;
import io.getarrayus.securecapita.repository.InvoiceRepository;
import io.getarrayus.securecapita.rowmapper.StatsRowMapper;
import io.getarrayus.securecapita.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static io.getarrayus.securecapita.query.CustomerQuery.CUSTOMER_STATS_QUERY;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getCustomers(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Customer> searchCustomer(String name, int page, int size) {
        return customerRepository.findByNameContaining(name, PageRequest.of(page, size));
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoice(int page, int size) {
        return invoiceRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public void addInvoiceToCustomer(Long customerId, Invoice invoice) {
        invoice.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (Objects.nonNull(customer)) {
            invoice.setCustomer(customer);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId).orElse(null);
    }

    @Override
    public Stats getStats() {
        return jdbc.queryForObject(CUSTOMER_STATS_QUERY, Map.of(), new StatsRowMapper());
    }
}
