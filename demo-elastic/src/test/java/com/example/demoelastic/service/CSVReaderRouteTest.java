package com.example.demoelastic.service;


import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
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

@CamelSpringBootTest
@SpringBootTest
@EnableAutoConfiguration
@MockEndpoints("direct:*")
@ExtendWith(MockitoExtension.class)
public class CSVReaderRouteTest {

	private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext context;

	@EndpointInject("mock:direct:csvFileProcessor")
	private MockEndpoint mockCsvFileProcessor;

	@EndpointInject("mock:direct:rejectFile")
	private MockEndpoint mockRejectFile;

	@EndpointInject("mock:direct:moveFile")
	private MockEndpoint mockMoveFiles;

	@Configuration
	static class TestConfig {

		RoutesBuilder route() {
			return new CSVReaderRoute();
		}
	}

	@BeforeEach
	public void setup() {

		producerTemplate = context.createProducerTemplate();
		mockMoveFiles.reset();
		mockCsvFileProcessor.reset();
		mockRejectFile.reset();

	}

	@Test
	void testWetherFileComponentMoveFileToFileProcessor() throws Exception {

		File file = new File("testFolder/input/test.csv");

		mockCsvFileProcessor.expectedMessageCount(0);
		producerTemplate.sendBodyAndHeader("file:inputFolder/products?noop=true", file, Exchange.FILE_NAME, file.getName());

		mockCsvFileProcessor.assertIsSatisfied();

	}

	@Test
	void testWeatherANonCsvCallMoveFile() throws Exception {

		File file = new File("testFolder/input/testTwo.csv");

		mockMoveFiles.expectedMessageCount(0);
		producerTemplate.sendBodyAndHeader("file:inputFolder/products?noop=true", file, Exchange.FILE_NAME, file.getName());
		mockMoveFiles.assertIsSatisfied();

	}

}
