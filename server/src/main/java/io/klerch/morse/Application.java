package io.klerch.morse;

import io.klerch.morse.config.JerseyConfig;
import io.klerch.morse.model.MorseCode;
import io.klerch.morse.utils.ImageUtils;
import io.klerch.morse.utils.S3Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
