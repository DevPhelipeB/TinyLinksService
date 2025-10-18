package br.com.dev.tiny.service.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "TinyLinks API",
        version = "1.0.0",
        description = "API para encurtamento de URLs",
        contact = @Contact(
            name = "Desenvolvedor",
            email = "devphelipe@outlook.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    )
)
public class TinyLinksApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinyLinksApplication.class, args);
	}

}
