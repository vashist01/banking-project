package com.banking.account.config;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignMtlsConfig {
  private final char[] keystore = "changeit".toCharArray();
  private final String keyStoreKey = "PKCS12";
  @Bean
  public CloseableHttpClient feignHttpClient() throws Exception {

    KeyStore keyStore =
        KeyStore.getInstance("PKCS12");

    try (InputStream is = getClass()
        .getClassLoader()
        .getResourceAsStream(
            "certs/account-keystore.p12")) {

      if (is == null) {
        throw new IllegalStateException(
            "account-keystore.p12 not found"
        );
      }

      keyStore.load(
          is,
          "changeit".toCharArray()
      );
    }

    KeyManagerFactory kmf =
        KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        );

    kmf.init(
        keyStore,
        "changeit".toCharArray()
    );

    KeyStore trustStore =
        KeyStore.getInstance("PKCS12");

    try (InputStream is = getClass()
        .getClassLoader()
        .getResourceAsStream(
            "certs/account-truststore.p12")) {

      if (is == null) {
        throw new IllegalStateException(
            "account-truststore.p12 not found"
        );
      }

      trustStore.load(
          is,
          "changeit".toCharArray()
      );
    }

    TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        );

    tmf.init(trustStore);

    SSLContext sslContext =
        SSLContext.getInstance("TLS");

    sslContext.init(
        kmf.getKeyManagers(),
        tmf.getTrustManagers(),
        null
    );

    SSLConnectionSocketFactory sslSocketFactory =
        new SSLConnectionSocketFactory(
            sslContext
        );

    return HttpClients.custom()
        .setSSLSocketFactory(sslSocketFactory)
        .build();
  }
}

