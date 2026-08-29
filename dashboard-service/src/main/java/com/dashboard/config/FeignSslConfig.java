package com.dashboard.config;

@Configuration
public class FeignSslConfig {

    @Bean
    public CloseableHttpClient feignHttpClient() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream is = new ClassPathResource(
                "certs/dashboard-keystore.p12"
        ).getInputStream()) {

            keyStore.load(is, "changeit".toCharArray());
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm()
                );

        keyManagerFactory.init(
                keyStore,
                "changeit".toCharArray()
        );

        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream is = new ClassPathResource(
                "certs/dashboard-truststore.p12"
        ).getInputStream()) {

            trustStore.load(is, "changeit".toCharArray());
        }

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );

        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null
        );

        SSLConnectionSocketFactory sslSocketFactory =
                SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(sslContext)
                        .build();

        return HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
    }
}