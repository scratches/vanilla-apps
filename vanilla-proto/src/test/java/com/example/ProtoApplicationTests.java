package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

import demo.CustomerProtos.Customer;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ProtoApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void get() {
		client.get().uri("/").exchange().expectBody(Customer.class)
				.value(customer -> assertThat(customer.getFirstName()).isNotNull());
	}

	@Test
	public void post() {
		Customer customer = Customer.newBuilder().setId(1).setFirstName("Juergen")
				.setLastName("Hoeller").build();
		client.post().uri("/").body(Mono.just(customer), Customer.class).exchange()
				.expectStatus().isAccepted();
		client.get().uri("/").exchange().expectBody(Customer.class)
				.value(value -> assertThat(value).isEqualTo(customer));
	}

	@Test
	public void binary() {
		/*
		 * Same result as this:
		 * 
		 * curl -v localhost:8080 -H
		 * "Content-Type: application/x-protobuf;messageType=demo.Customer"
		 * --data-binary @src/test/resources/customer.data
		 */
		Customer customer = Customer.newBuilder().setId(0).setFirstName("Dave")
				.setLastName("Syer").build();
		client.post().uri("/")
				.body(BodyInserters.fromResource(new ClassPathResource("customer.data")))
				.exchange().expectStatus().isAccepted();
		client.get().uri("/").exchange().expectBody(Customer.class)
				.value(value -> assertThat(value).isEqualTo(customer));
	}

}
