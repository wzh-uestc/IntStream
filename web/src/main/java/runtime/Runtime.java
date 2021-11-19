package runtime;

import com.alibaba.fastjson.JSON;
import runtime.driver.Driver;
import runtime.driver.DriverConfig;
import runtime.job.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runtime {
    public static String etcPath;
    public static String jobDir;

    public static HashMap<String, JobInfo> job_mp = new HashMap<>();
    public static HashMap<String, String> job_codes_mp = new HashMap<>();

    public static void setEtcPath(String p){
        etcPath = p;
    }

    public static void setJobDir(String p){
        jobDir = p;
    }

    public static List<Driver> getAvailableDrivers(){
        return DriverConfig.getDriverConfig(etcPath + "drivers_config.json");
    }

    public static List<JobSummary> getJobLists(){
        List<JobSummary> list = new ArrayList<>();
        for(Map.Entry<String, JobInfo> e: job_mp.entrySet()){
            JobInfo jobInfo = e.getValue();
            JobSummary jobSummary = new JobSummary(jobInfo.getId(), jobInfo.getName(), jobInfo.getStart_timestamp(), jobInfo.getEnd_timestamp(), jobInfo.getStatus());
            list.add(jobSummary);
        }

        return list;
    }

    public static JobInfo getJobInfo(String jobId){
        if(job_mp.containsKey(jobId)){
            return job_mp.get(jobId);
        }
        else {
            return null;
        }
    }

    public static void addJobInfo(JobInfo jobInfo){
        if(jobInfo != null) {
            job_mp.put(jobInfo.getId(), jobInfo);
        }
    }

    /**
     * get specific type file (etc. jar or java) form dir
     * @return
     */
    public static String getFilePath(String dir, String type) throws IOException{
        File dirFile = new File(dir);
        if(dirFile.exists() && dirFile.isDirectory() && dirFile.listFiles() != null){
            for(File f: dirFile.listFiles()){
                System.out.println(f.getName());
                if(f.isFile() && f.getName().contains(type)){
                    return f.getCanonicalPath();
                }
            }
        }
        return null;
    }

    public static void tarJobtoDirectory(String jobFilePath, String targetDir){
        try {
            File outPath = new File(targetDir);
            if (!outPath.exists()) {
                outPath.mkdirs();
            }

            String cmdStr = "unzip " + jobFilePath + " -d " + targetDir;
            java.lang.Runtime run = java.lang.Runtime.getRuntime();

            Process process = run.exec(cmdStr);
            InputStream in = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(reader);
            StringBuffer ret = new StringBuffer();
            String line;
            while((line = br.readLine()) != null) {
                if(line.contains("id") && line.contains("name") && line.contains("start_timestamp") && line.contains("end_timestamp")) {
                    ret.append(line);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static JobInfo parseJobInfo(String jobId){
        try {
            String jobPath = Runtime.jobDir + jobId;

            System.out.println(jobPath);

            String jarFilePath = getFilePath(jobPath, "jar");
            String javaFilePath = getFilePath(jobPath, "java");
            System.out.println(jarFilePath);
            System.out.println(javaFilePath);
            if(jarFilePath == null || javaFilePath == null){
                return null;
            }

            String cmdStr = "java -jar " + jarFilePath + " runtime " + javaFilePath;
            java.lang.Runtime run = java.lang.Runtime.getRuntime();

            Process process = run.exec(cmdStr);
            InputStream in = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(reader);
            StringBuffer ret = new StringBuffer();
            String line;
            while((line = br.readLine()) != null) {
                if(line.contains("id") && line.contains("name") && line.contains("start_timestamp") && line.contains("end_timestamp")) {
                    ret.append(line);
                }
            }
            System.out.println("ret: " + ret);
            if(ret.toString().length() > 0) {
                JobInfo jobInfo = JSON.parseObject(ret.toString(), JobInfo.class);
                //reset jobId
                jobInfo.setId(jobId);
                return jobInfo;
            }
            else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //return JobConfig.getJobConfig();
    }

    public static boolean deleteJob(String jobId){
        if(job_mp.containsKey(jobId)){
            job_mp.remove(jobId);
            job_codes_mp.remove(jobId);
            deleteDir(new File(jobDir + jobId));
            return true;
        }
        return false;
    }

    public static String parseJobCode(String jobId){
        try{
            String jobPath = Runtime.jobDir + jobId;

            String javaFilePath = getFilePath(jobPath, "java");
            if(javaFilePath == null){
                return null;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(javaFilePath));

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

    public static void addJobCode(String jobId, String jobCode){
        job_codes_mp.put(jobId, jobCode);
    }



    public static String getCodeString(String jobId){
        if(job_codes_mp.containsKey(jobId)){
            return job_codes_mp.get(jobId);
        }
        else{
            return null;
        }
    }


}
