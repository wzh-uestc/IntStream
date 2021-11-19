package core;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ElasticSearchHandler {
    public static String url = "http://[2001:da8:ff:212::41:21]:9200/";
    public static String index1 = "telemetry_runtime/_doc/";

    public static String getRuntimeStatisticsEndPoint(){
        return url + index1;
    }

    public static void insert(String endpoint, Object o) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            String jsonString = JSON.toJSONString(o);
            StringEntity stringEntity = new StringEntity(jsonString);
            httpPost.setEntity(stringEntity);

            HttpResponse response = httpclient.execute(httpPost);
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
                //System.out.println(line);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String job_name = "minehunter";
        String lsp_name = "stenographer_s0_129";


        //RuntimeStatistics t = new RuntimeStatistics("2021-01-06T16:14:12", job_name, lsp_name, 100, 90, 91.5, 111111);


        //ElasticSearchHandler.insert(ElasticSearchHandler.getRuntimeStatisticsEndPoint(), t);
    }
}
