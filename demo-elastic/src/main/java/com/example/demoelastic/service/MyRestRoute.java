package com.example.demoelastic.service;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demoelastic.model.Product;

@Component
public class MyRestRoute extends RouteBuilder {
        @Value("${authenticationKey}")
        String authenticationKey;
        
        String loggingRoute="direct:loggingCurrentheaderandbody";
        @Override
        public void configure() throws Exception {

                rest("/product")
                                .get("getAll")
                                .to("direct:getAll")
                                .get("getByid/{id}")
                                .to("direct:getById")

                                .post("addOne/{id}")
                                .consumes("application/json").type(Product.class)
                                .to("direct:InsertData")
                                .post("addDoc")
                                .consumes("text/plain")
                                .to("direct:addDocument")

                                .put("update/{id}")
                                .type(Product.class)
                                .to("direct:UpdateData")

                                .delete("deleteByid/{id}")
                                .produces("application/json")
                                .to("direct:deleteById");

                from("direct:deleteById")
                                .log(LoggingLevel.INFO,"Request to delete ${header.id}.")
                                .doTry()
                                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                                .setHeader(Exchange.AUTHENTICATION, simple(authenticationKey))
                                .toD("{{camel.elasticindice}}/_doc/${header.id}?bridgeEndpoint=true")
                                .log(LoggingLevel.INFO,"Deletion Completed.")
                                .doCatch(HttpOperationFailedException.class)
                                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("404"))
                                .setBody().simple("{\"errorMessage\":\"No Such Object\"}")
                                .log(LoggingLevel.ERROR,"${exception.message}")
                                .to(loggingRoute);

                from("direct:addDocument")
                                .log(LoggingLevel.INFO,"Request to a new Document.")
                                .process(e -> {
                                        String document = e.getIn().getBody(String.class);
                                        HashMap<String, Object> map = new HashMap<>();
                                        HashMap<String, String> innermap = new HashMap<>();
                                        innermap.put("review", document);
                                        map.put("doc", innermap);
                                        e.getIn().setBody(map);
                                })
                                .marshal().json()
                                .to(loggingRoute)
                                .to("direct:InsertData");

        }

}
