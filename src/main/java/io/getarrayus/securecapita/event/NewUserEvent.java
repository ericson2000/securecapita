package io.getarrayus.securecapita.event;

import io.getarrayus.securecapita.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {

    private String email;
    private EventType type;

    public NewUserEvent(String email, EventType type) {
        super(email);
        this.type = type;
        this.email = email;
    }
}
