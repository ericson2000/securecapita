package io.getarrayus.securecapita.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
public class UserEvent {
    private Long id;

    private String type;

    private String description;

    private String device;

    private String ipAddress;

    private LocalDateTime createdAt;
}
