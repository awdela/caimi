package com.caimi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	public static String httpGet(String url) throws IOException{
		HttpGet httpGet = new HttpGet(url);
		//Map<String,String> map = new HashMap<String, String>();
		//setGetHead(httpGet,map);
		return execute(httpGet);
	}
	private static String execute(HttpUriRequest request) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(execute(request,baos)<0) {
			return null;
		}
		return new String(baos.toByteArray());
	}
	private static int execute(HttpUriRequest request,OutputStream os) throws IOException {	
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//�����û���֤
		httpclient = createCloseableHttpClientWithBasicAuth(httpclient);
		CloseableHttpResponse response = null;
		HttpEntity entity=  null;
		try {
			response = httpclient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = null;
			entity = response.getEntity();
			if(statusCode!=HttpStatus.SC_OK) {
				throw new IOException("Execute http request " + request + " failed, response status: " + statusCode + " body: " + responseBody);
			}
			if(entity!=null) {
				int totalLen = 0;
				InputStream in = entity.getContent();
				byte[] data = new byte[1024];
				int len = 0;
				while((len=in.read(data))>0){
					os.write(data,0,len);
					totalLen += len;
				}
				os.flush();
				return totalLen;
			}else {
				return -1;
			}
		}finally {
			try {
				EntityUtils.consume(entity);
			}catch (IOException e) {}
			
			if(response!=null) {
				try {
					response.close();
				}catch (IOException e) {}
				
			}
		}
	}
	public static CloseableHttpClient createCloseableHttpClientWithBasicAuth(CloseableHttpClient closeableHttpClient){  
        // ����HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        CredentialsProvider provider = new BasicCredentialsProvider();  
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);  
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");  
        provider.setCredentials(scope, credentials);  
        httpClientBuilder.setDefaultCredentialsProvider(provider);  
        closeableHttpClient = httpClientBuilder.build();
        return closeableHttpClient;
    }  
//����http��Head
//	private static void setGetHead(HttpGet httpGet, Map<String,String> headMap) {
//		if(headMap!=null && headMap.size()!=0) {
//			Set<String> sets = headMap.keySet();
//			for(String set:sets) {
//				httpGet.addHeader(set, headMap.get(set));
//			}
//		}
//	}
}
