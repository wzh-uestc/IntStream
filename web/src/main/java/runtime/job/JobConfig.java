package runtime.job;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobConfig {
    public static JobInfo getJobConfig(){
        return getJobConfig("etc/job_plan.json");
    }

    public static JobInfo getJobConfig(String path){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            String line;
            String jsonString = "";
            while ((line = bufferedReader.readLine()) != null) {
                jsonString += line;
            }

            //jsonString = "{\"end_timestamp\":-1,\"id\":\"909f6d8f-b465-4ae0-a1ac-077962f97b96\",\"job_plan\":{\"gsp_plan\":null,\"lsp_plans\":[{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]},{\"sink\":{\"name\":\"LSP Kafka Producer\\nTopic: test\\nServer Address: [2001:Da8:ff:212::41:21]:9092\"},\"source\":{\"name\":\"StenographerDriver\\nPath: \\\"/data/packets/\\\"\\nStart Timestamp: 1608270614000000\\nEnd Timestamp: -1\"},\"transforms\":[{\"name\":\"Window Transformer\\tTime Window: 10\"}]}]},\"name\":\"minehunter\",\"start_timestamp\":111,\"status\":\"ready\"}\n";

            return JSON.parseObject(jsonString, JobInfo.class);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getJobCode(String path){
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        //JobInfo j = getJobConfig();
        System.out.println(getJobCode("data/Minehunter.java"));
    }
}
