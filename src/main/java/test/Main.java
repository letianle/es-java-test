package test;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest.Builder;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * todo
 *
 * @author tianle
 * @date 2024/4/9
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        // URL and API key
        String serverUrl = "http://localhost:9200";

        // Create the low-level client
        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        BooleanResponse products = esClient.indices().exists(i -> i.index("products"));
        boolean value = products.value();
        log.info("indices product exists {}", value);
        if (value) {
            //todo query index product bk-1
            Product product = new Product("bk-1", "City bike", 123.0);
            GetResponse<Product> getResponse = esClient.get(q -> q.index("products").id(product.sku), Product.class);

            if (getResponse.found()) {
            }


            IndexResponse response = esClient.index(i -> i
                    .index("products")
                    .id(product.getSku())
                    .document(product)
            );
            log.info("Indexed with version {}", response.version());
        } else {
            //Creating an index
            esClient.indices().create(c -> c.index("products"));
        }


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Product {
        private String sku;
        private String name;
        private double price;
    }
}