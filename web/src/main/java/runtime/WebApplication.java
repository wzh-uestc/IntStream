package runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import runtime.job.JobConfig;
import runtime.job.JobInfo;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) throws Exception {
        //Runtime.setEtcPath(args[0]);
        Runtime.setEtcPath("etc/");
        Runtime.setJobDir("data/");

        //initialize minehunter job
        /*
        JobInfo jobInfo =  Runtime.parseJobInfo("909f6d8f-b465-4ae0-a1ac-077962f97b96");
        jobInfo.setStart_timestamp(1608270614);
        jobInfo.setStatus("running");
        */

        JobInfo j = Runtime.parseJobInfo("909f6d8f-b465-4ae0-a1ac-077962f97b96");
        j.setStart_timestamp(1608270614);
        j.setStatus("running");
        Runtime.addJobInfo(j);

        Runtime.addJobCode(j.getId(), Runtime.parseJobCode("909f6d8f-b465-4ae0-a1ac-077962f97b96"));

        SpringApplication.run(WebApplication.class, args);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize(DataSize.ofMegabytes(200));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(200));
        return factory.createMultipartConfig();
    }
}
