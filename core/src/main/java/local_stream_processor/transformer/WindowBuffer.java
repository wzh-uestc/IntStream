package local_stream_processor.transformer;

import org.apache.flink.api.java.functions.KeySelector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

public class WindowBuffer <T> {
    private HashMap<String, LinkedList<T>> keyedStream_mp = new HashMap<>();
    private LinkedList<T> stream_list = new LinkedList<>();

    private Function<T, Long> extractTimestampFunc = null;
    private KeySelector<T, String> keySelector = null;
    private int windowInterval = 0;
    private long windowStartTimestamp = -1;
    private boolean begin_flag = false;

    /*
    public WindowBuffer(Function<T, Long> extractTimestampFunc, Function<T, String> extractKeyFunc, int windowInterval){
        this.extractKeyFunc = extractKeyFunc;
        this.extractTimestampFunc = extractTimestampFunc;
        this.windowInterval = windowInterval;
    }
    */

    public WindowBuffer(Function<T, Long> extractTimestampFunc, KeySelector<T, String> keySelector, int windowInterval){
        this.keySelector = keySelector;
        this.extractTimestampFunc = extractTimestampFunc;
        this.windowInterval = windowInterval;
    }

    public boolean add(T t){
        /*
        //check if packet is during the window interval
        long date = extractTimestampFunc.apply(t);
        if(!begin_flag){
            windowStartTimestamp = date - (date % windowInterval);
            begin_flag = true;
        }
        else {
            if(date - windowStartTimestamp >= windowInterval){
                return false;
            }
        }

        //stream -> keyedstream
        if(extractKeyFunc == null){
            stream_list.add(t);
        }
        else {
            String key = extractKeyFunc.apply(t);
            LinkedList<T> linkedList = keyedStream_mp.get(key);

            if(linkedList == null){
                linkedList = new LinkedList<>();
                linkedList.add(t);
                keyedStream_mp.put(key, linkedList);
            }
            else {
                linkedList.add(t);
            }
        }

        return true;
        */

        //check if packet is during the window interval
        try {
            long timestamp = extractTimestampFunc.apply(t);
            if (!begin_flag) {
                windowStartTimestamp = timestamp - (timestamp % windowInterval);
                begin_flag = true;
            } else {
                if (timestamp - windowStartTimestamp >= windowInterval) {
                    return false;
                }
            }

            //stream -> keyedstream
            if (keySelector == null) {
                stream_list.add(t);
            } else {
                String key = keySelector.getKey(t);
                LinkedList<T> linkedList = keyedStream_mp.get(key);

                if (linkedList == null) {
                    linkedList = new LinkedList<>();
                    linkedList.add(t);
                    keyedStream_mp.put(key, linkedList);
                } else {
                    linkedList.add(t);
                }
            }

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<LinkedList<T>> getKeyedStream(){
        /*
        ArrayList<LinkedList<T>> res = new ArrayList<>();
        if(extractKeyFunc == null){
            res.add(stream_list);
            return res;
        }
        else {
            res.addAll(keyedStream_mp.values());
            return res;
        }
        */

        ArrayList<LinkedList<T>> res = new ArrayList<>();
        if(keySelector == null){
            res.add(stream_list);
            return res;
        }
        else {
            res.addAll(keyedStream_mp.values());
            return res;
        }
    }

    public void clear(){
        this.windowStartTimestamp = -1;
        this.begin_flag = false;
        this.keyedStream_mp.clear();
        this.stream_list.clear();
    }
}
