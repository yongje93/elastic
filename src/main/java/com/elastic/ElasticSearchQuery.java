package com.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ElasticSearchQuery {

    private final ElasticsearchClient elasticsearchClient;

    private final String indexName = "products";

    public String createOrUpdateDocument(Product product) throws IOException {
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(product.getId())
                .document(product)
        );

        if (response.result().name().equals("Created")) {
            return "Document has been successfully created.";
        } else if (response.result().name().equals("Updated")) {
            return "Document has been successfully updated.";
        } else {
            return "Error while performing the operation.";
        }
    }

    public Product getDocumentById(String productId) throws IOException {
        Product product = null;

        GetResponse<Product> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(productId),
                Product.class);

        if (response.found()) {
            product = response.source();
            System.out.println("product.getName() = " + Objects.requireNonNull(product).getName());
        } else {
            System.out.println("Product not found");
        }

        return product;
    }

    public String deleteDocumentById(String productId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d
                .index(indexName)
                .id(productId)
        );

        DeleteResponse deleteResponse = elasticsearchClient.delete(request);

        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return "Product with id: " + deleteResponse.id() + " has been deleted.";
        } else {
            System.out.println("Product not found");
            return "Product with id: " + deleteResponse.id() + " does not exist.";
        }
    }

    public List<Product> searchAllDocuments() throws IOException {
        List<Product> products = new ArrayList<>();
        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
        SearchResponse<Product> searchResponse = elasticsearchClient.search(searchRequest, Product.class);
        List<Hit<Product>> hits = searchResponse.hits().hits();
        for (Hit<Product> hit : hits) {
            products.add(hit.source());
        }
        return products;
    }
}
