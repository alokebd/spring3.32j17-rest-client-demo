package com.vision.spring.restclient.client;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClient;


@Configuration
public class RestClientConfig {
	private static final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);
	@Value("${application.truststore.path}")
	private String trustStore;
	@Value("${application.keystore.password}")
	private String tsPassword;
	
	@Value("${application.timeout}")
	private String connTimeOut;
	@Value("${application.readtimeout}")
	private String readTimeOut;
	
	@Value("${application.resource.base-url}")
	private String apiBaseUrl;
	public static final int DEFAULR_CONN_KEEP_ALIVE= 10000;
	
	@Bean (name="restClient")
	public RestClient restClient() {
		return  RestClient.create();
	}
	

    @Bean (name="defaultRestClient")
    public RestClient defaultRestClient() {
    	logger.info("clientHttpRequestRestClient base url:"+apiBaseUrl);
    	
        return RestClient
                .builder()
                /*
                .baseUrl(apiBaseUrl)
               .defaultHeaders(
                        httpHeaders -> {
                          httpHeaders.set("Content-Type", "application/json");
                        })
                */
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Integer.parseInt(readTimeOut));
        factory.setConnectTimeout(Integer.parseInt(connTimeOut));
        logger.info("ClientHttpRequestFactory factory loadded successfully");
        return factory;
    }
    
    @Bean (name="defaultCloseableHttpClientRestClient")
	RestClient httpComponentsClientHttpRestClient() {
		logger.info("defaultCloseableHttpClientRestClient base url:"+apiBaseUrl);
	    return RestClient.builder()
	      .baseUrl(apiBaseUrl)
	      //.requestInterceptor(...)
	      //.defaultHeader("AUTHORIZATION", fetchToken())
	      //.messageConverters(...)
	      .requestFactory(httpComponentsClientHttpRequestFactory())
	      .build();
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
		  HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		  CloseableHttpClient httpClient = HttpClients.createDefault();
		  factory.setHttpClient(httpClient);
		  logger.info("httpComponentsClientHttpRequestFactory factory loadded successfully");
		  return factory;
	}

        
    @Bean (name="secureCloseableHttpRestClient")
    public RestClient secureCloseableHttpRestClient() throws  Exception {
      	//SSL context
    	SSLContext sslContext = SSLContextBuilder
    			.create()
    			.loadTrustMaterial(ResourceUtils.getFile(trustStore), tsPassword.toCharArray()).build();
    	//SSL factory
    	SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
    	
    	//Build SSL factory
    	Registry<ConnectionSocketFactory> sslFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
    			.register("https", sslSocketFactory)
    			.build();
    	
    	//Create Connection Pool
    	PoolingHttpClientConnectionManager connectionPool = new PoolingHttpClientConnectionManager(sslFactoryRegistry);
    	
    	connectionPool.setMaxTotal(10);
    	connectionPool.setDefaultMaxPerRoute(10);
    	RequestConfig requestConfig = RequestConfig.custom()
    			.setConnectionRequestTimeout(30000, TimeUnit.MILLISECONDS)
    			.build();
    	//Create Closeable Http client
    	CloseableHttpClient closeableHttpClient = HttpClients.custom()
    			.setConnectionManager(connectionPool)
    			.setDefaultRequestConfig(requestConfig)
    			.evictIdleConnections(TimeValue.ofMilliseconds(15000))
    			.setKeepAliveStrategy(getConnectionKeepAliveStrategy())
    			.build();
    	
    	//Spring Http Client Request factory
    	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    	requestFactory.setConnectionRequestTimeout(Integer.parseInt(connTimeOut));
       	requestFactory.setHttpClient(closeableHttpClient);
    	
       	//Build RestClient with Spring Http Client Request factory
       	RestClient restClient=  RestClient.builder()
      		  // .requestFactory(new HttpComponentsClientHttpRequestFactory(closeableHttpClient))
       		  .requestFactory(requestFactory)
       		  .defaultHeaders(
                    httpHeaders -> {
                      httpHeaders.set("Content-Type", "application/json");
                    })
    		  // .requestFactory(getClientHttpRequestFactory())
      		  .build();
       	logger.info("CloseableHttpRestClient Bean loadded successfully");
       	return restClient;
    }
    
    private ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
    	return new ConnectionKeepAliveStrategy() {

			@Override
			public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
				// TODO Auto-generated method stub
				BasicHeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
				 while (iterator.hasNext()) {
			            HeaderElement he = iterator.next();
			            String param = he.getName();
			            String value = he.getValue();
			            if (value != null && param.equalsIgnoreCase("timeout")) {
			               try {
			                    return TimeValue.ofMicroseconds(Long.parseLong(value)* 1000);
			                } 
			                catch(NumberFormatException ignore) {
			                	return TimeValue.ofMicroseconds(DEFAULR_CONN_KEEP_ALIVE);
			                }
			            }
			        }
				return TimeValue.ofMicroseconds(DEFAULR_CONN_KEEP_ALIVE);
			}
    		
    	};
    }
    
    /*
    @Bean (name="sslRestClient")
    public RestClient sslRestClient(RestClient.Builder restClientBuilder, SslBundles sslBundles) {
        ClientHttpRequestFactorySettings requestFactorySettings = new ClientHttpRequestFactorySettings(
                Duration.ofSeconds(30),
                Duration.ofSeconds(30),
                sslBundles.getBundle("mybundle"));

        return restClientBuilder
                .requestFactory(ClientHttpRequestFactories.get(requestFactorySettings))
                .build();
    }
    */
}
