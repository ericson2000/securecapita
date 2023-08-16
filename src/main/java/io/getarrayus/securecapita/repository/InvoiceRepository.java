package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.domain.Invoice;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Repository
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {
}
