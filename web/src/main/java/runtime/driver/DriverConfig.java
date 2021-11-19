package runtime.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import runtime.driver.Driver;

import java.io.*;
import java.util.List;

public class DriverConfig {

    public static List<Driver> getDriverConfig(){
        return getDriverConfig("etc/drivers_config.json");
    }

    public static List<Driver> getDriverConfig(String path){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            String line;
            String jsonString = "";
            while ((line = bufferedReader.readLine()) != null) {
                jsonString += line;
            }

            JSONObject object = JSON.parseObject(jsonString);
            return JSON.parseArray(object.getJSONArray("drivers").toJSONString(), Driver.class);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
