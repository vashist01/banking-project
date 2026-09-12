package com.dashboard.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

  @Bean
  public ExecutorService dashboardExecutor() {
    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(
                10,                       // corePoolSize
                20,                       // maximumPoolSize
                60,                       // keepAliveTime
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
  }
}
