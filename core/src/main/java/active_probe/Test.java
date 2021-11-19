package active_probe;

public class Test {
    public static void main(String[] args) throws Exception{
        PingDriver pingDriver = new PingDriver();
        PingResult pingResult = pingDriver.sendPing("www.baidu.com", 3);
        System.out.println(pingResult);
    }
}
