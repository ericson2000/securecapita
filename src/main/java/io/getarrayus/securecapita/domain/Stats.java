package io.getarrayus.securecapita.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
//@JsonInclude(NON_DEFAULT)
public class Stats {

    private int totalCustomers;

    private int totalInvoices;

    private double totalBilled;
}
