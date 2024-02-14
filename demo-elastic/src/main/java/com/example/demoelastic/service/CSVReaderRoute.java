package com.example.demoelastic.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

import com.example.demoelastic.model.Product;

@Component
public class CSVReaderRoute extends RouteBuilder {

    String loggingRoute="direct:loggingCurrentheaderandbody";
    @Override
    public void configure() throws Exception {
        DataFormat bind = new BindyCsvDataFormat(Product.class);

        from("file:inputFolder/products?noop=true").routeId("csv-input")
                .log(LoggingLevel.INFO, "A new file Received.")
                .doTry()
                .unmarshal(bind)
                .to("direct:csvFileProcessor")
                .endDoTry()
                .doCatch(IllegalArgumentException.class)
                .to(loggingRoute)
                .log(LoggingLevel.ERROR, "Failed to parse csv file")
                .to("direct:rejectFile");

        from("direct:csvFileProcessor")
                .to(loggingRoute)
                .split(body())
                .to(loggingRoute)
                .log(LoggingLevel.INFO, "Processing request ${body}")
                .process(e -> {
                    Product product = e.getIn().getBody(Product.class);
                    e.getIn().setHeader("id", product.getId());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("doc", product);
                    e.getIn().setBody(map);
                })
                .marshal().json()
                .to(loggingRoute)
                .to("direct:InsertData")
                .log(LoggingLevel.INFO, "Response ${body}")
                .end()
                .log(LoggingLevel.INFO, "Processing Completed.")
                .to("direct:moveFile");

        from("direct:rejectFile")
                .process(e -> {
                    String filename = e.getIn().getHeader("CamelFileName", String.class);
                    Path sourcePath = Paths.get("inputFolder/products/" + filename);
                    Path destinationPath = Paths.get("outputFolder/failed/" + filename);
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                })
                .log(LoggingLevel.INFO, "Moved file to outputFolder/failed.")
                .to(loggingRoute);

        from("direct:moveFile")
                .process(e -> {
                    String filename = e.getIn().getHeader("CamelFileName", String.class);
                    Path sourcePath = Paths.get("inputFolder/products/" + filename);
                    Path destinationPath = Paths.get("outputFolder/done/" + filename);
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                })
                .log(LoggingLevel.INFO, "Moved file to outputFolder/done")
                .to(loggingRoute);
    }

}
