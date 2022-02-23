package com.gcc.airbase.esserver;


import com.gcc.airbase.config.ElasticSearchProperties;
import com.gcc.airbase.config.model.EsCondition;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class LoadEsClient {

    @Autowired
    ElasticSearchProperties properties;

    @Conditional(EsCondition.class)
    @Bean
    public RestHighLevelClient getClient() {
        RestHighLevelClient highLevelClient = null ;
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getUser(), properties.getPassword()));
            highLevelClient = new RestHighLevelClient(
                    RestClient.builder(getESHosts()).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                        @Override
                        public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                            // 设置连接超时和套接字超时时长
                            return requestConfigBuilder.setConnectTimeout(60000).setSocketTimeout(60000*3);
                        }
                    }).setHttpClientConfigCallback(
                            new RestClientBuilder.HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                }
                            }
                    )
            );
        return highLevelClient;
    }


    @ConditionalOnProperty(name="airbase.esserver-enable", havingValue="true")
    @Bean
    public RestClient getRestClient() {
        RestClient restClient = null;
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getUser(), properties.getPassword()));
            SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
            restClient = RestClient.builder(getESHosts()).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    // 设置连接超时和套接字超时时长
                    return requestConfigBuilder.setConnectTimeout(60000).setSocketTimeout(60000*3);
                }
            }).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            }).setFailureListener(sniffOnFailureListener).build();

            Sniffer sniffer = Sniffer.builder(restClient)
                    .setSniffAfterFailureDelayMillis(30000)
                    .build();
            sniffOnFailureListener.setSniffer(sniffer);

        return restClient;
    }

    @ConditionalOnProperty(name="airbase.esserver-enable", havingValue="true")
    @Bean
    public TransportClient getTransportClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name",true)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(properties.getHost()),properties.getTcpPort()));

        return client;
    }


    private HttpHost[] getESHosts() {
        String[] esHosts = properties.getHost().trim().split(",");
        List<HttpHost> host = new ArrayList<HttpHost>();
        for(String it:esHosts){
            host.add(new HttpHost(it,properties.getPort(), properties.getHttpScheme()));
        }
        HttpHost[] hostArray = host.toArray(new HttpHost[esHosts.length]);
        return hostArray;
    }


}
