package active_probe;

import java.util.*;

public class StrategyTest {
    public static HashSet<String> sipSet = new HashSet<>();
    public static HashSet<String> dipSet = new HashSet<>();
    public static List<String[]> probingPaths = new LinkedList<>();

    public static ArrayList<Long>[] fullMeshTimes;
    public static ArrayList<Long>[] randomMeshTimes;

    public static void initSipSet(int num){
        sipSet.clear();
        int cnt = 0;
        for(int i = 0;i < num;i++){
            sipSet.add(String.valueOf(cnt++));
        }
    }

    public static void initDipSet(int num){
        dipSet.clear();
        int cnt = 0;
        for(int i = 0;i < num;i++){
            dipSet.add(String.valueOf(cnt++));
        }
    }

    public static void fullMesh(){
        probingPaths.clear();
        for(String sip: sipSet){
            for(String dip: dipSet){
                probingPaths.add(new String[] {sip, dip});
            }
        }
    }

    public static String[] shuffle(HashSet<String> set){
        String[] arrs = new String[set.size()];
        int cnt = 0;
        for(String s: set){
            arrs[cnt++] = s;
        }

        Random random = new Random();
        for(int i = 0;i < arrs.length;i++){
            int random_index = random.nextInt(arrs.length - i) + i;
            String tmp = arrs[i];
            arrs[i] = arrs[random_index];
            arrs[random_index] = tmp;
        }

        return arrs;
    }

    public static void randomMesh(int N){
        probingPaths.clear();
        String[] shuffledDips = shuffle(dipSet);
        int index = 0;
        for(String sip: sipSet){
            if(index + N >= shuffledDips.length){
                shuffledDips = shuffle(dipSet);
                index = 0;
            }

            //random cnt
            int cnt = N < shuffledDips.length ? N : shuffledDips.length;
            for(int i = 0;i < cnt;i++){
                probingPaths.add(new String[] {sip, shuffledDips[index++]});
            }
        }
    }

    public static long[] test(int sipnum, int dipnum, int N){
        initSipSet(sipnum);
        initDipSet(dipnum);

        long t1 = System.currentTimeMillis();
        fullMesh();
        long t2 = System.currentTimeMillis();

        long t3 = System.currentTimeMillis();
        randomMesh(N);
        long t4 = System.currentTimeMillis();

        return new long[] {t2 - t1, t4 - t3};
    }

    public static long testFullmesh(int sipnum, int dipnum){
        initSipSet(sipnum);
        initDipSet(dipnum);

        long t1 = System.currentTimeMillis();
        fullMesh();
        long t2 = System.currentTimeMillis();

        return t2 - t1;
    }

    public static long testRandomCover(int sipnum, int dipnum, int d){
        initSipSet(sipnum);
        initDipSet(dipnum);

        long t1 = System.currentTimeMillis();
        randomMesh(d);
        long t2 = System.currentTimeMillis();

        return t2 - t1;
    }

    public static void main(String[] args){
        /*
        fullMeshTimes = new ArrayList[20];
        randomMeshTimes = new ArrayList[20];

        int k = 20;

        for(int i = 0;i < 20;i++){
            fullMeshTimes[i] = new ArrayList<>();
            randomMeshTimes[i] = new ArrayList<>();
            int num = (i + 1) * 100;

            for(int t = 0; t < k;t++) {
                //System.out.print(num + ": ");
                long[] times = test(num, num, 20);
                //for (int i = 0; i < 2; i++) {
                    //System.out.print(times[i] + " ");
                //}
                //System.out.println();

                fullMeshTimes[i].add(times[0]);
                randomMeshTimes[i].add(times[1]);
            }
        }

        double[] avg1s = new double[20];
        double[] avg2s = new double[20];

        for(int i = 0;i < 20;i++){
            long sum1 = 0, sum2 = 0;
            for(int t = 0;t < k;t++){
                sum1 += fullMeshTimes[i].get(t);
                sum2 += randomMeshTimes[i].get(t);
            }

            double avg1 = sum1 / 10;
            double avg2 = sum2 / 10;

            avg1s[i] = avg1;
            avg2s[i] = avg2;

            //System.out.println(i + ": " + avg1 + " " + avg2);

        }

        for(int i = 0;i < 20;i++){
            System.out.print(avg1s[i] + ", ");
        }
        System.out.println();

        for(int i = 0;i < 20;i++){
            System.out.print(avg2s[i] + ", ");
        }
        System.out.println();
        */


        ArrayList<Double> res = new ArrayList<>();
        for(int i = 0;i < 10;i++){
            int num = (i + 1) * 10;

            long sum = 0;
            for(int k = 0;k < 5000;k++){
                sum += testRandomCover(num, num, 20);
            }

            //System.out.print((double)sum / 5000 + ", ");
            res.add((double)sum / 5000);
        }

        Collections.sort(res);
        System.out.println(res);
    }
}
