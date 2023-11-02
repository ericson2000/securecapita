package io.getarrayus.securecapita.rest;

import com.sun.net.httpserver.Headers;
import io.getarrayus.securecapita.domain.Customer;
import io.getarrayus.securecapita.domain.HttpResponse;
import io.getarrayus.securecapita.domain.Invoice;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.report.CustomerReport;
import io.getarrayus.securecapita.service.CustomerService;
import io.getarrayus.securecapita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@RestController
@RequestMapping(path = "/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDto userDto, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "page", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                                "stats", customerService.getStats()))
                        .message("Customers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDto userDto, @RequestBody Customer customer) {
        return ResponseEntity.created(URI.create(""))
                .body(HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "customer", customerService.createCustomer(customer)))
                        .message("Customer created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDto userDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "customer", customerService.getCustomer(id)))
                        .message("Customer retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomer(@AuthenticationPrincipal UserDto userDto, Optional<String> name, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "page", customerService.searchCustomer(name.orElse(""), page.orElse(0), size.orElse(10))))
                        .message("Customers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDto userDto, @RequestBody Customer customer) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "customer", customerService.updateCustomer(customer)))
                        .message("Customer updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/invoice/created")
    public ResponseEntity<HttpResponse> createInvoice(@AuthenticationPrincipal UserDto userDto, @RequestBody Invoice invoice) {
        return ResponseEntity.created(URI.create(""))
                .body(HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "invoice", customerService.createInvoice(invoice)))
                        .message("Invoice created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    @GetMapping("/invoice/list")
    public ResponseEntity<HttpResponse> getInvoices(@AuthenticationPrincipal UserDto userDto, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "page", customerService.getInvoice(page.orElse(0), size.orElse(10))))
                        .message("Invoice retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/invoice/new")
    public ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDto userDto) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "customers", customerService.getCustomers()))
                        .message("Customers retrived")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/invoice/get/{id}")
    public ResponseEntity<HttpResponse> getInvoice(@AuthenticationPrincipal UserDto userDto, @PathVariable("id") Long id) {
        var invoice = customerService.getInvoice(id);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "invoice", invoice,
                                "customer", invoice.getCustomer()))
                        .message("Invoice retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/invoice/addtocustomer/{id}")
    public ResponseEntity<HttpResponse> addInvoiceToCustomer(@AuthenticationPrincipal UserDto userDto, @PathVariable("id") Long customerId, @RequestBody Invoice invoice) {
        customerService.addInvoiceToCustomer(customerId, invoice);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(userDto.getEmail()),
                                "customers", customerService.getCustomers()))
                        .message(String.format("Customer added to invoice with ID: %s", customerId))
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport() {
        List<Customer> customers = new ArrayList<>();
        customerService.getCustomers().iterator().forEachRemaining(customers::add);
        CustomerReport report = new CustomerReport(customers);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", "customer-report.xlsx");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=customer-report.xlsx");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ns-excel"))
                .headers(headers)
                .body(report.export());

    }


}
