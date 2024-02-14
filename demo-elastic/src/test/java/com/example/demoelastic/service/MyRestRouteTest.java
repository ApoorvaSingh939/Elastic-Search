package com.example.demoelastic.service;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demoelastic.model.Product;

@CamelSpringBootTest
@SpringBootTest
@EnableAutoConfiguration
@MockEndpoints("direct:*")
@ExtendWith(MockitoExtension.class)
public class MyRestRouteTest {
    private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext context;

	@EndpointInject("mock:direct:deleteById")
	private MockEndpoint mockdeleteById;

	@EndpointInject("mock:direct:addDocument")
	private MockEndpoint mockAddDocument;

	@Configuration
	static class TestConfig {

		RoutesBuilder route() {
			return new CSVReaderRoute();
		}
	}

	@BeforeEach
	public void setup() {

		producerTemplate = context.createProducerTemplate();
		mockdeleteById.reset();
		mockAddDocument.reset();

	}

	// @Test
	void calladdData() throws Exception {
        getProduct();
		mockAddDocument.expectedMessageCount(0);
		producerTemplate.sendBodyAndHeader("direct:addDocument","This is my new doc",null,null);
		mockAddDocument.assertIsSatisfied();
	}
    @Test
    void callDocumentAdd(){
//todo
    }
    @Test
    void InsertInvalidPayload(){
//todo  
    }

	@Test
	void deleteById() throws Exception {
        Product product=new Product();
		mockdeleteById.expectedMessageCount(1);
		producerTemplate.sendBodyAndHeader("direct:deleteById",product,"id","2");
		mockdeleteById.assertIsSatisfied();
	}

    public Product getProduct() {
        Product product=new Product();
        product.setId("2");
        product.setColour("Blue");
        product.setPrice("200");
        product.setProductName("Pen");
        return product;
    }
}
