package com.example.demoelastic.restclient;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demoelastic.model.Product;

import static org.junit.Assert.assertNotNull;
import javax.inject.Inject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ElasticRestServiceTest {

    @Inject
    private ElasticRestService elasticRestService;

    @Order(1)
    @Test
    public void testFindProduct() throws Exception {
        String str=elasticRestService.findProduct("5");
        assertNotNull(str);
    }
    @Order(2)
    @Test
    public void testAddProduct() throws Exception {
        Product product=new Product();
        product.setId("22");
        product.setColour("White");
        product.setPrice("600");
        product.setProductName("book");
        String str=elasticRestService.newProduct(product);
        assertNotNull(str);
    }
    @Order(3)
    @Test
    public void updateProduct() throws Exception {
        Product product=new Product();
        product.setId("22");
        product.setColour("Black");
        product.setPrice("400");
        product.setProductName("book");
        String str=elasticRestService.updateProduct("22",product);
        assertNotNull(str);
    }
    @Order(4)
    @Test
    public void deleteProduct() throws Exception {
        String str=elasticRestService.deleteProduct("22");
        assertNotNull(str,"deleted");
    }
    @Order(5)
    @Test
    public void failtodeleteProduct() throws Exception {
        String str=elasticRestService.deleteProduct("22");
        assertNotNull(str,"Failed to delete comment");
    }
    @Order(6)
    @Test
    public void FailedupdateProduct() throws Exception {
        Product product=new Product();
        product.setId("22");
        product.setColour("Black");
        product.setPrice("400");
        product.setProductName("book");
        String str=elasticRestService.updateProduct("22",product);
        assertNotNull(str);
        assert(str.contains("A ElsaticserachException Occures ::"));
    }
    
}
