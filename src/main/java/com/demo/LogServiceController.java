package com.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.ssl.SSLContexts;
import java.security.cert.*;
//import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@RestController
public class LogServiceController {

	@PostMapping(consumes = "application/json", value = "/push")
	public String pushLogs(@RequestBody RequestModel logRequest) {
       try {
		RestTemplate template = new RestTemplate();
		StringBuilder url = new StringBuilder("https://").append(logRequest.getHost()).append(":")
				.append(logRequest.getPort()).append("/").append(logRequest.getUri());
		ResponseEntity<String> response = template.getForEntity(url.toString(), String.class);
		
		System.out.println(response.getBody());
       }catch(Exception e) {
    	   e.printStackTrace();
       }

		return "Success";
	}
	
	@GetMapping(value="/get")
	public String getLogs() {
		 try {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(csf)
				.build();

			HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);
			
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			String url = "https://elasticsearch.ibm-common-services.svc:9200/_cluster/health?wait_for_status=yellow&timeout=50s&pretty";
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
				
			System.out.println(response.getBody());
			 
			return response.getBody();
		       }catch(Exception e) {
					e.printStackTrace();
					return e.getMessage();
		       }
		 ///return "Failure";
		
	}

}
