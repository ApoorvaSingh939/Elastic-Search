package com.example.demoelastic.restclient;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demoelastic.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ElasticRestServiceTest {

    @Inject
    private ElasticRestService elasticRestService;

    @Inject 
    ObjectMapper objectMapper;

    private String getCountFromSearchResult(String str) {
        str=str.replace("SearchResponse: ","");
        try {
            TreeNode treeNode=objectMapper.readTree(str);
            String count=treeNode.get("hits").get("total").get("value").toString();
            return count;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "0";
    }
    @Order(1)
    @Test
    public void testFindProduct() throws Exception {
        String str=elasticRestService.findProduct("22");
        String count=getCountFromSearchResult(str);
        assertEquals("0", count);
    }
    
    @Order(2)
    @Test
    public void testAddProduct() throws Exception {
        Product product=new Product();
        product.setId("22");
        product.setColour("White");
        product.setPrice("600");
        product.setProductName("book");
        elasticRestService.newProduct(product);
        String str=elasticRestService.findProduct("22");
        String count=getCountFromSearchResult(str);
        assertEquals("1", count);
        assertTrue(str.contains("colour=White"));
        assertTrue(str.contains("price=600"));
        assertTrue(str.contains("productName=book"));
    }
    @Order(3)
    @Test
    public void updateProduct() throws Exception {
        Product product=new Product();
        product.setId("22");
        product.setColour("Black");
        product.setPrice("400");
        product.setProductName("book");
        elasticRestService.updateProduct("22",product);
        String str=elasticRestService.findProduct("22");
        String count=getCountFromSearchResult(str);
        assertEquals("1", count);
    }
    @Order(4)
    @Test
    public void deleteProduct() throws Exception {
        elasticRestService.deleteProduct("22");
        String str=elasticRestService.findProduct("22");
        String count=getCountFromSearchResult(str);
        assertEquals("0", count);
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
        assert(str.contains("A ElsaticserachException Occures ::"));
    }
    
}
