package de.htwberlin.webtech.korbgeld;

import org.springframework.boot.SpringApplication;

public class TestKorbgeldApplication {

	public static void main(String[] args) {
		SpringApplication.from(KorbgeldApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
