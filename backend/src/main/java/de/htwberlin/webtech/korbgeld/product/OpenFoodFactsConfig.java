package de.htwberlin.webtech.korbgeld.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class OpenFoodFactsConfig {

    @Bean
    public RestClient openFoodFactsRestClient(@Value("${app.open-food-facts.base-url}") String baseUrl,
                                              @Value("${app.name}") String appName) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", appName + "/0.1 (HTW Berlin Studienprojekt)")
                .build();
    }
}
