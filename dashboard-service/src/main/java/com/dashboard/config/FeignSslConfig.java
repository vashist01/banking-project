package com.dashboard.config;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FeignSslConfig {

    @Bean
    public CloseableHttpClient feignHttpClient() throws Exception {

        // =========================
        // 1. Dashboard keystore
        // =========================
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream =
                     new ClassPathResource(
                             "certs/dashboard-keystore.p12"
                     ).getInputStream()) {

            keyStore.load(
                    inputStream,
                    "changeit".toCharArray()
            );
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm()
                );

        keyManagerFactory.init(
                keyStore,
                "changeit".toCharArray()
        );


        // =========================
        // 2. Dashboard truststore
        // =========================
        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream =
                     new ClassPathResource(
                             "certs/dashboard-truststore.p12"
                     ).getInputStream()) {

            trustStore.load(
                    inputStream,
                    "changeit".toCharArray()
            );
        }

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );

        trustManagerFactory.init(trustStore);


        // =========================
        // 3. SSL Context
        // =========================
        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null
        );


        // =========================
        // 4. SSL Socket Factory
        // =========================
        SSLConnectionSocketFactory sslSocketFactory =
                SSLConnectionSocketFactoryBuilder
                        .create()
                        .setSslContext(sslContext)
                        .build();


        // =========================
        // 5. Connection Manager
        // =========================
        HttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder
                        .create()
                        .setSSLSocketFactory(sslSocketFactory)
                        .build();


        // =========================
        // 6. HttpClient
        // =========================
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }
}