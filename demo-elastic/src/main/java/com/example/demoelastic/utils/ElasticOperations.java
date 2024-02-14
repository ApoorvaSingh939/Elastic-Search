package com.example.demoelastic.utils;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElasticOperations extends RouteBuilder {
        @Value("${authenticationKey}")
        String authenticationKey;
        
        String loggingRoute="direct:loggingCurrentheaderandbody";
    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"Error :: ${exception}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.statusCode}"))
                .setBody(simple("${exception.message}"));

        from("direct:InsertData")
                .log(LoggingLevel.INFO, "Inserting Data.")
                .log(LoggingLevel.INFO,"${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.AUTHENTICATION, simple(authenticationKey))
                .toD("{{camel.elasticindice}}/_doc/${header.id}?bridgeEndpoint=true")
                .log("done")
                .to(loggingRoute);

        from("direct:getAll")
                .log(LoggingLevel.INFO, "Getting Data.")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.AUTHENTICATION, simple(authenticationKey))
                .toD("{{camel.elasticindice}}/_search?bridgeEndpoint=true")
                .log("done")
                .to(loggingRoute);

        from("direct:getById")
                .log(LoggingLevel.INFO, "Getting Id ${header.id}.")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.AUTHENTICATION, simple(authenticationKey))
                .toD("{{camel.elasticindice}}/_doc/${header.id}?bridgeEndpoint=true")
                .log("done")
                .to(loggingRoute);

        from(loggingRoute).routeId("headerandbodylogger")
        .log(LoggingLevel.INFO, "current body : ${body}")
        .log(LoggingLevel.DEBUG, "current headers : ${headers}");

    }

}
