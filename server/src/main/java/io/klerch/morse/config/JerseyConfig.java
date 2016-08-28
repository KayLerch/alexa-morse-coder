package io.klerch.morse.config;

import io.klerch.morse.service.EncodeService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(EncodeService.class);
    }
}
