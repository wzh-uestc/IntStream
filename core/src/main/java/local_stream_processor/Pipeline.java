package local_stream_processor;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class Pipeline<T> {
    private LinkedBlockingDeque<Object> queue = new LinkedBlockingDeque<>();
    private String name;
    private int push_cnt = 0;
    private int pop_cnt = 0;
    private boolean max_flag = false;

    private static int id = 0;

    public Pipeline(){
        this.name = "pipe" + id++;
    }

    public void push(T t){
        //System.out.println("[Pipeline] " + name + " [Push " + push_cnt++ +"] " + t);

        try {
            //queue.put(t);
            boolean flag = queue.offer(t);
            if(!flag && !max_flag){
                System.out.println("[Pipeline max]: " + name + "\tsize: " + queue.size());
                max_flag = true;
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public T pop(){

        try {
            T t =  (T)queue.take();
            //System.out.println("[Pipeline] " + name + " [Pop " + pop_cnt++ +"] " + t);

            return t;
        }
        catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return "[Pipeline]\tName: " + name;
    }
}
