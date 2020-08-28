package com.honeywell.bookconsumer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Date;


@Configuration
public class BookConsumer {

    @Value("${target.endpoint}")
    String targetEndpoint;

    @Value("${token.stored.value}")
    String tokenValue;

    private static final String JWT_KEY = "super secret key";

    @EventListener(ApplicationStartedEvent.class)
    public void doStuff(){
//        String authenticationToken ="Bearer "+bakeJWT();
        consumeAll();
        addBook("Przygody dobrego wojaka Szwejka");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addBook("XYZ");
        consumeAll();
    }

    public void consumeAll(){
        RestTemplate restTemplate = new RestTemplate();
        String booksResponse = restTemplate.exchange(targetEndpoint, HttpMethod.GET,null, String.class).getBody();
        System.out.println("Fetched books:"+System.lineSeparator()+booksResponse);
    }

    public void addBook(String title){
        HttpHeaders headers = new HttpHeaders();
        String authenticationToken ="Bearer "+bakeJWT();
        headers.add("Authorization", authenticationToken);
        headers.add("Content-Type", "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(title, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(targetEndpoint, HttpMethod.POST,entity, String.class);
    }

    private String bakeJWT(){
        Algorithm algo = Algorithm.HMAC256(JWT_KEY);
        String token = JWT.create().withSubject("application name").withIssuedAt(new Date()).withExpiresAt(new Date(System.currentTimeMillis()+10000)).sign(algo);
        System.out.println("Token{"+token+"}");
        return token;
    }
}
