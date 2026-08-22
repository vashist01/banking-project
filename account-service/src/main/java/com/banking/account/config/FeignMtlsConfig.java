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
  public CloseableHttpClient feignHttpClient() throws Exception{
    KeyStore keyStore = KeyStore.getInstance(keyStoreKey);
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("certs/account-keystore.p12")){
      if(inputStream == null ){
        throw new IllegalStateException("account-keystore.p12 not found");
      }
      keyStore.load(inputStream,keystore);
    }
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore,keystore);

    KeyStore trustKeyStore = KeyStore.getInstance(keyStoreKey);
    try(InputStream inputStream = getClass().getResourceAsStream("certs/account-truststore.p12")){
      if(inputStream ==null){
        throw new IllegalStateException("account-truststore.p12 not found");
      }
      trustKeyStore.load(inputStream,keystore);
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(trustKeyStore);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(),null);
      SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
      return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }
  }

}
