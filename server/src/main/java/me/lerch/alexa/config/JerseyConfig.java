package me.lerch.alexa.config;

import me.lerch.alexa.service.EncodeService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(EncodeService.class);
    }
}
