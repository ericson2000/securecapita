package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {

    Page<Customer> findByNameContaining(String name, Pageable pageable);
}
