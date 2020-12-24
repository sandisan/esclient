package com.demo;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LogServiceController {
	
	@Autowired
	private RestTemplate restTemplate;
	 
	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };  
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom()); 
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();   
        HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
        customRequestFactory.setHttpClient(httpClient);
        return builder.requestFactory(() -> customRequestFactory).build();  
    }

	@PostMapping(consumes = "application/json", value = "/push")
	public String pushLogs(@RequestBody RequestModel logRequest) {
       try {
		//RestTemplate template = new RestTemplate();
		StringBuilder url = new StringBuilder("https://").append(logRequest.getHost()).append(":")
				.append(logRequest.getPort()).append("/").append(logRequest.getUri());
		ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
		
		System.out.println(response.getBody());
       }catch(Exception e) {
    	   e.printStackTrace();
       }

		return "Success";
	}
	
	@GetMapping(value="/get")
	public String getLogs() {
		 try {
				//RestTemplate template = new RestTemplate();
				String url = "https://elasticsearch.ibm-common-services.svc:9200/_cluster/health";
				//String url = "https://elasticsearch.ibm-common-services.svc/_cluster/health";
				ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
				
				System.out.println(response.getBody());
				return response.getBody();
		       }catch(Exception e) {
			 String stacktrace = e.printStackTrace();
			 return stacktrace;
		    	 // e.printStackTrace();
		       }
		 
		
	}

}
