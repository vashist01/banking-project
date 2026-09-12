package com.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DashboardServiceApplication {

	public static void main(String[] args) {
		 SpringApplication application =
                new SpringApplication(DashboardServiceApplication.class);

        application.setApplicationStartup(
                new BufferingApplicationStartup(2048)
        );

		application.run(args);
	}

}
