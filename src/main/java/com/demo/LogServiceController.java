package com.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LogServiceController {

	@Autowired
	private RestTemplate restTemplate;

//	@Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) throws NoSuchAlgorithmException, KeyManagementException {
//
//        TrustManager[] trustAllCerts = new TrustManager[] {
//                new X509TrustManager() {
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return new X509Certificate[0];
//                    }
//                    public void checkClientTrusted(
//                            java.security.cert.X509Certificate[] certs, String authType) {
//                    }
//                    public void checkServerTrusted(
//                            java.security.cert.X509Certificate[] certs, String authType) {
//                    }
//                }
//        };  
//        SSLContext sslContext = SSLContext.getInstance("SSL");
//        sslContext.init(null, trustAllCerts, new java.security.SecureRandom()); 
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .build();   
//        HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
//        customRequestFactory.setHttpClient(httpClient);
//        return builder.requestFactory(() -> customRequestFactory).build();  
//    }

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
		char[] password = "elk".toCharArray();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ "+ System.getProperty("java.class.path"));
		SSLContext sslContext = SSLContextBuilder.create()
				.loadKeyMaterial(keyStore("classpath:elk.jks", password), password)
				//.loadKeyMaterial(keyStore(System.getProperty("user.dir")+"/target/classes/elk.jks", password), password)
				.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		System.out.println("############# SSLContext set ###########");
		HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();

		return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client)).build();
	}

	private KeyStore keyStore(String file, char[] password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		File key = ResourceUtils.getFile(file);
		System.out.println("############# Loaded JKS ###########");
		try (InputStream in = new FileInputStream(key)) {
			keyStore.load(in, password);
		}
		System.out.println("############# Returning JKS ###########");
		return keyStore;
	}

	@PostMapping(consumes = "application/json", value = "/push")
	public String pushLogs(@RequestBody RequestModel logRequest) {
		try {
			// RestTemplate template = new RestTemplate();
			StringBuilder url = new StringBuilder("https://").append(logRequest.getHost()).append(":")
					.append(logRequest.getPort()).append("/").append(logRequest.getUri());
			ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);

			System.out.println(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Success";
	}

	@GetMapping(value = "/get")
	public String getLogs() {
		try {
			// RestTemplate template = new RestTemplate();
			String url = "https://elasticsearch.ibm-common-services.svc:9200/_cluster/health";
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			System.out.println(response.getBody());
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		//return "Failure";

	}

}
