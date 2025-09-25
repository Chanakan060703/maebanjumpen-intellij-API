package com.itsci.mju.maebanjumpen;

import com.fasterxml.jackson.databind.SerializationFeature; // อาจจะมีหรือไม่มีก็ได้
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // อาจจะมีหรือไม่มีก็ได้
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaebanjumpenApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaebanjumpenApplication.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			builder.modules(new JavaTimeModule()); // บรรทัดนี้สำคัญมาก
			// builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // บรรทัดนี้จะไม่มีผลกับปัญหาปัจจุบัน
		};
	}
}