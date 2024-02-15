package com.example.demoelastic.restclient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import com.example.demoelastic.model.Product;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/restClient")
@Service
public class ElasticRestService {

    @Value("${IndexWithoutMapping}")
    String index1;

    private final ElasticsearchClient esClient;

    @Autowired
    public ElasticRestService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @PostMapping("/addProduct")
    public String newProduct(@RequestBody Product product) throws IOException {

        log.info("Received add Request", product.toString());
        IndexRequest<Product> commentReq = IndexRequest.of((id -> id
                .id(product.getId())
                .index(index1)
                .refresh(Refresh.WaitFor)
                .document(product)));
        log.info("created Index Request id :{} String : {}", commentReq.id(), commentReq.toString());
        IndexResponse indexResponse = esClient.index(commentReq);
        log.info("add Response {}", indexResponse.toString());
        return indexResponse.toString();
    }

    @PutMapping("/updateProduct")
    public String updateProduct(@RequestParam(name = "id") String productId, @RequestBody Product product) {

        log.info("Updating id :{}  to {}", productId, product.toString());
        UpdateResponse<Product> updateResponse;
        try {
            updateResponse = esClient.update(u -> u
                    .index(index1)
                    .id(productId)
                    .doc(product),
                    Product.class);
        } catch (ElasticsearchException | IOException e) {
            return "A ElsaticserachException Occures :: " + e.getLocalizedMessage();
        }
        log.info("UpdateResponse : {}", updateResponse.toString());
        return updateResponse.toString();
    }

    @DeleteMapping("/deleteById")
    public String deleteProduct(@RequestParam(name = "id") String productId) throws IOException {
        log.info("Received delete Request for id", productId);
        DeleteByQueryResponse deleteProduct = esClient.deleteByQuery(ss -> ss
                .index(index1)
                .waitForCompletion(true)
                .refresh(true)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .term(t -> t
                                                .field("id")
                                                .value(productId))))));
        log.info("Delete Doc Request for id :{} String : {}", productId, deleteProduct.toString());
        if (deleteProduct.deleted() < 1) {
            return ("Failed to delete product");
        }
        return "deleted";
    }

    @GetMapping("/getData")
    public String findProduct(@RequestParam(name = "id") String productId) throws IOException {
        SearchResponse<Product> commentsByArticle = esClient.search(s -> s
                .index(index1)
                .query(q -> q
                        .term(t -> t
                                .field("id")
                                .value(productId))),
                Product.class);
        log.info("Get Response for id {} : {} ", productId, commentsByArticle.toString());
        return commentsByArticle.toString();
    }
}
