package com.example.demoelastic.restclient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticClient {

        @Value("${elasticsearch.server.url}")
        private String serverUrl;

        @Value("${elasticsearch.api.key}")
        private String apiKey;

        @Value("${IndexWithoutMapping}")
        String index1;

        @Value("${IndexWithMapping}")
        String index2;

        /**
         * Creates the ElasticsearchClient and the indexes needed
         *
         * @return a configured ElasticsearchClient
         */
        @Bean
        public ElasticsearchClient elasticRestClient() throws IOException {

                RestClient restClient = RestClient
                                .builder(HttpHost.create(serverUrl))
                                .setDefaultHeaders(new Header[] {
                                                new BasicHeader("Authorization", "ApiKey " + apiKey)
                                })
                                .build();
                ObjectMapper mapper = JsonMapper.builder()
                                .addModule(new JavaTimeModule())
                                .build();

                ElasticsearchTransport transport = new RestClientTransport(
                                restClient, new JacksonJsonpMapper(mapper));

                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                createSimpleIndex(esClient, index1);
                createIndexWithDateMapping(esClient, index2);
                return esClient;
        }

        private void createSimpleIndex(ElasticsearchClient esClient, String index) throws IOException {
                BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
                if (!indexRes.value()) {
                        esClient.indices().create(c -> c
                                        .index(index));
                }
        }

        private void createIndexWithDateMapping(ElasticsearchClient esClient, String index) throws IOException {
                BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
                if (!indexRes.value()) {
                        esClient.indices().create(c -> c
                                        .index(index)
                                        .mappings(m -> m
                                                        .properties("createdAt", p -> p
                                                                        .date(d -> d.format(
                                                                                        "strict_date_optional_time")))
                                                        .properties("updatedAt", p -> p
                                                                        .date(d -> d.format(
                                                                                        "strict_date_optional_time")))));

                }
        }
}