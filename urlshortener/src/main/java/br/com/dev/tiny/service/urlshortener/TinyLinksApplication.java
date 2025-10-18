package br.com.dev.tiny.service.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "TinyLinks API", version = "1.0", description = "API for managing TinyLinks"))
public class TinyLinksApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinyLinksApplication.class, args);
	}

}
