package com.wholeseeds.mindle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(
	basePackages = "com.wholeseeds.mindle.domain",
	repositoryBaseClass = JpaBaseRepositoryImpl.class
)
public class MindleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindleApplication.class, args);
	}

}
