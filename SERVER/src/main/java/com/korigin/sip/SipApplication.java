package com.korigin.sip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SipApplication {

	public static void main(String[] args) {
		SpringApplication.run(SipApplication.class, args);
	}

}
