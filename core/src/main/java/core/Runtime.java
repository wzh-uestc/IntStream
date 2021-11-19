package core;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.sun.management.OperatingSystemMXBean;
import local_stream_processor.transformer.AbstractTransformer;

public class Runtime extends Thread {
    private ExecutionEnvironment executionEnvironment;
    private Thread thread;

    public Runtime(ExecutionEnvironment executionEnvironment){
        this.executionEnvironment = executionEnvironment;
    }

    public void run(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long last_source_cnt = 0;
            long last_sink_cnt = 0;

            ArrayList<AbstractTransformer<?, ?>> transformers = executionEnvironment.getCurLocalEnvironment().getLocalExecutionPlan().getTransformers();
            int size = transformers.size();
            long[] lastTransformerCnts = new long[size];

            while (true) {
                long timestamp = System.currentTimeMillis();
                String date_h = sdf.format(new Date(timestamp));
                String date = date_h.replace(" ", "T") + ".000+08:00";
                String job_name = executionEnvironment.getJobName();
                String lsp_name = executionEnvironment.getLocalCmd();

                //get cnt
                long source_cnt = executionEnvironment.getCurLocalEnvironment().getLocalExecutionPlan().getNetSource().getCnt();
                long sink_cnt = executionEnvironment.getCurLocalEnvironment().getLocalExecutionPlan().getNetSink().getCnt();
                long interval_source_cnt = source_cnt - last_source_cnt;
                long interval_sink_cnt = sink_cnt - last_sink_cnt;

                long[] transformerCnts = new long[size];
                for(int i = 0;i < size;i++){
                    transformerCnts[i] = transformers.get(i).getCnt();
                }

                //cpu and memory
                OperatingSystemMXBean mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                double cpu_utilization = mxBean.getProcessCpuLoad();

                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                long memory_usage = memoryMXBean.getHeapMemoryUsage().getUsed();

                //output the runtime statistics
                StringBuilder sb = new StringBuilder();
                sb.append(date_h + ", " + cpu_utilization + ", " + memory_usage + ", " + interval_source_cnt + ", " + source_cnt + ", ");
                for(int i = 0;i < size;i++){
                    sb.append(String.valueOf(transformerCnts[i] - lastTransformerCnts[i]) + ", " + transformers.get(i).getCnt() + ", ");
                }
                sb.append(interval_sink_cnt + ", " + sink_cnt);
                System.out.println(sb.toString());
                executionEnvironment.statisticsOutputStream.write((sb.toString() + "\n").getBytes());


                /*
                * send the runtime statistics to elastic search
                System.out.println("date: " + date_h + "\tjob_name: " + job_name + "\tlsp_name: " + lsp_name
                         + "\tsource_cnt: " + source_cnt + "\tsink_cnt: " + sink_cnt + "\tinterval_source_cnt: " + interval_source_cnt
                        + "\tinterval_sink_cnt: " + interval_sink_cnt + "\tcpu_utilization: " + cpu_utilization
                        + "\tmemory_usage: " + memory_usage);

                RuntimeStatistics r = new RuntimeStatistics(date, job_name, lsp_name, source_cnt, sink_cnt, interval_source_cnt, interval_sink_cnt, cpu_utilization, memory_usage);
                ElasticSearchHandler.insert(ElasticSearchHandler.getRuntimeStatisticsEndPoint(), r);
                */

                Thread.sleep(10 * 1000);

                //flush last cnt
                last_source_cnt = source_cnt;
                last_sink_cnt = sink_cnt;
                for(int i = 0;i < size;i++){
                    lastTransformerCnts[i] = transformerCnts[i];
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void start(){
        if(thread == null){
            thread = new Thread(this, "NetSource");
            thread.start();
        }
    }
}
