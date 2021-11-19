package runtime;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import runtime.driver.Driver;
import runtime.job.JobSummary;
import runtime.job.JobInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@RestController
public class WebController {
    @RequestMapping(value="/driver/list")
    public List<Driver> getAvailableDrivers(){
        return Runtime.getAvailableDrivers();
    }

    @RequestMapping(value="/job/list")
    public List<JobSummary> getJobLists(){
        return Runtime.getJobLists();
    }

    @RequestMapping(value="/job/info/{job_id}")
    public JobInfo getJobInfo(@PathVariable(value = "job_id") String job_id){
        return Runtime.getJobInfo(job_id);
    }

    @RequestMapping(value="/job/code/{job_id}")
    public String getJobCode(@PathVariable(value = "job_id") String job_id){
        return Runtime.getCodeString(job_id);
    }

    @RequestMapping(value="/job/delete",method = RequestMethod.POST)
    public String handlowJobDelete(@RequestParam("job_id") String job_id){
        System.out.println("/job/delete" + job_id);
        if(Runtime.deleteJob(job_id)){
            return "Delete Success: " + job_id;
        }
        else {
            return "Delete Fail, no such job.";
        }
    }

    @RequestMapping(value="/job/submit",method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file){
        System.out.println("/job/submit: " + file.getOriginalFilename());

        if (!file.isEmpty()) {
            try {
                // save zip file, rename tmp
                /*
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(Runtime.jobDir + "tmp.zip")));
                out.write(file.getBytes());
                out.flush();
                out.close();
                */

                //tar tmp.zip to directory random ID
                String jobId = UUID.randomUUID().toString();
                System.out.println(jobId);
                Runtime.tarJobtoDirectory(Runtime.jobDir + "tmp.zip", Runtime.jobDir + jobId);

                //parse jobInfo and code, and add to Runtime's jobs
                JobInfo jobInfo =  Runtime.parseJobInfo(jobId);
                String code = Runtime.parseJobCode(jobId);
                if(jobInfo != null) {
                    Runtime.addJobInfo(jobInfo);
                    Runtime.addJobCode(jobId, code);

                    return "File upload success: " + file.getOriginalFilename();
                }
                else{
                    // jobinfo parse error, remove dir
                    System.out.println("jobinfo parse error, remove dir");
                    Runtime.deleteDir(new File(Runtime.jobDir + jobId));
                    return "File error: not a telemetry job file";
                }
            } catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        } else {
            return "Error: File is Empty";
        }
    }


}
