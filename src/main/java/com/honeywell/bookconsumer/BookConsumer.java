package com.honeywell.bookconsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;



@Configuration
public class BookConsumer {

    @Value("${target.endpoint}")
    String targetEndpoint;

    @Value("${token.stored.value}")
    String tokenValue;

    @EventListener(ApplicationStartedEvent.class)
    public void doStuff(){
        String authenticationToken ="Bearer "+tokenValue;
        consumeAll();
        addBook("Przygody dobrego wojaka Szwejka", authenticationToken);
        addBook("XYZ", authenticationToken);
        consumeAll();
    }

    public void consumeAll(){
        RestTemplate restTemplate = new RestTemplate();
        String booksResponse = restTemplate.exchange(targetEndpoint, HttpMethod.GET,null, String.class).getBody();
        System.out.println("Fetched books:"+System.lineSeparator()+booksResponse);
    }

    public void addBook(String title, String authenticationToken){
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", authenticationToken);
        headers.add("Content-Type", "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(title, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(targetEndpoint, HttpMethod.POST,entity, String.class);
    }
}
