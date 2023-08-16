package io.getarrayus.securecapita.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String services;

    private Date date;

    private String status;

    private double total;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonInclude
    private Customer customer;

}
