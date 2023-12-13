package com.musdon.SecureBankingSystem;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Secure Banking System",
				description = "Backend REST APIs for Bank App",
				version="1.0",
				contact = @Contact(
						name = "Omar Khaled Abdelfatah",
						email = "omarkabdelfatah@gmail.com",
						url= "https://github.com/omarkabdelfatah"
				),
				license = @License(
						name="Secure Banking System",
						url= "https://github.com/omarkabdelfatah"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Secure Banking System Documentation",
				url= "https://github.com/omarkabdelfatah"
		)
)
public class SecureBankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureBankingSystemApplication.class, args);
	}

}
