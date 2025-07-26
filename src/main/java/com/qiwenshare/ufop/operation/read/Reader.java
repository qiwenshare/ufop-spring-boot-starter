package com.qiwenshare.ufop.operation.read;

import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Reader {
    public abstract String read(ReadFile readFile);


    public InputStream downloadAsStream(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                return new ByteArrayInputStream(bytes);
            }
        }
    }
}
