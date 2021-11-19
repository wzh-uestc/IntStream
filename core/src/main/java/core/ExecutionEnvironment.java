package core;

import com.alibaba.fastjson.JSON;
import core.job.ExecutionPlan;
import core.job.ExecutionUnit;
import core.job.JobInfo;
import core.job.JobPlan;
import local_stream_processor.LocalEnvironment;
import local_stream_processor.sink.NetSink;
import local_stream_processor.source.NetSource;
import local_stream_processor.transformer.AbstractTransformer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import primitives.GSPStream;
import primitives.NetStream;

import java.io.*;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

public class ExecutionEnvironment {
    private HashMap<String, LocalEnvironment> lovalEnvironments = new HashMap<>();
    private StreamExecutionEnvironment globalEnvironment;
    private Runtime runtime;

    private String jobName;
    private String jobId;
    private String localCmd;
    private LocalEnvironment curLocalEnvironment = null;

    private String sourceCodePath;

    //runtime is off by default
    private boolean runtimeFlag = false;

    //runtime statistics result output
    FileOutputStream statisticsOutputStream = null;

    public ExecutionEnvironment(String[] args) throws IOException{
        this.globalEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        this.runtime = new Runtime(this);

        localCmd = args[0];

        if(localCmd.equals("runtime")){
            sourceCodePath = args[1];
        }
        else{
            if(args.length >= 2){
                statisticsOutputStream = new FileOutputStream(new File(args[1]));
            }
        }
    }

    public ExecutionEnvironment() throws IOException{
        this.globalEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        this.runtime = new Runtime(this);

        localCmd = "local";
    }

    public void addLocalEnvironment(String name){
        this.lovalEnvironments.put(name, new LocalEnvironment(name));
    }

    public LocalEnvironment get(String name){
        return this.lovalEnvironments.get(name);
    }

    public StreamExecutionEnvironment getGlobalEnvironment(){
        return this.globalEnvironment;
    }

    public void execute(String jobName){
        this.jobName = jobName;
        this.jobId = "";

        if(localCmd.equals("runtime")){
            //print jobinfo
            this.outputJobInfoJson();
        }
        else {
            //execute lsp
            if(runtimeFlag == true) {
                this.runtime.start();
            }

            this.curLocalEnvironment = this.lovalEnvironments.get(localCmd);
            if (this.curLocalEnvironment != null) {
                System.out.println(localCmd + "execute");
                curLocalEnvironment.execute();
            }
        }
    }

    public NetStream<String> getGlobalNetStream(String topic){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), properties);
        flinkKafkaConsumer.setStartFromEarliest();

        DataStream<String> stream = this.globalEnvironment.addSource(flinkKafkaConsumer);

        return new GSPStream<>(stream);
    }

    public LocalEnvironment getLocalEnvironment(String name){
        return this.lovalEnvironments.get(name);
    }


    public void printExcutionPlans(){
        for(Map.Entry<String, LocalEnvironment> entry: this.lovalEnvironments.entrySet()){
            System.out.println("==============================");
            entry.getValue().getLocalExecutionPlan().print();
        }
    }


    /**
     * output the json file of Job detatil info
     */
    private void outputJobInfoJson(){
        JobInfo jobInfo = new JobInfo();
        jobInfo.setName(jobName);
        jobInfo.setId(jobId);
        jobInfo.setStatus("ready");
        jobInfo.setStart_timestamp(-1);
        jobInfo.setEnd_timestamp(-1);

        JobPlan jobPlan = new JobPlan();


        HashMap<String, String> primitives_mp = getPrimitivesFromSourceCode(this.sourceCodePath);

        //lsp_plans
        List<ExecutionPlan> lsp_plans = new ArrayList<>();

        for(Map.Entry<String, LocalEnvironment> entry: this.lovalEnvironments.entrySet()){
            NetSource netSource = entry.getValue().getLocalExecutionPlan().getNetSource();
            NetSink netSink = entry.getValue().getLocalExecutionPlan().getNetSink();
            List<AbstractTransformer<?,?>> transformers = entry.getValue().getLocalExecutionPlan().getTransformers();

            ExecutionPlan plan = new ExecutionPlan();
            plan.setSource(new ExecutionUnit(netSource.toString()));
            plan.setSink(new ExecutionUnit(netSink.toString()));

            List<ExecutionUnit> ts = new ArrayList<>();
            for(AbstractTransformer<?,?> t: transformers){
                String unit = t.toString();
                String name = "" + unit;

                if(primitives_mp != null && primitives_mp.containsKey(unit)){
                    name = unit + "\n" + primitives_mp.get(unit);
                }

                ts.add(new ExecutionUnit(name));
            }
            plan.setTransforms(ts);

            lsp_plans.add(plan);
        }

        jobPlan.setLsp_plans(lsp_plans);
        jobInfo.setJob_plan(jobPlan);

        String objStr = JSON.toJSONString(jobInfo, WriteMapNullValue);
        System.out.println(objStr);
    }


    private HashMap<String, String> getPrimitivesFromSourceCode(String path){
        try{
            HashMap<String, String> primitives_mp = new HashMap<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains(".filter(")){
                    primitives_mp.put("Filter Transformer", line.trim());
                }
                else if(line.contains(".setExtractKeyFunc(") || line.contains(".setTimeWindow(")){
                    String tmp = primitives_mp.get("Window Transformer");
                    if(tmp == null){
                        primitives_mp.put("Window Transformer", line.trim());
                    }
                    else{
                        primitives_mp.put("Window Transformer", tmp + "\n" + line.trim());
                    }
                }
            }

            return primitives_mp;
        }
        catch (IOException e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public LocalEnvironment getCurLocalEnvironment() {
        return curLocalEnvironment;
    }

    public String getJobName() {
        return jobName;
    }

    public String getLocalCmd() {
        return localCmd;
    }

    public String getJobId() {
        return jobId;
    }
}
