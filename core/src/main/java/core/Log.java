package core;

public class Log {
    public static void println(String message){
        System.out.println(message);
    }

    public static void printError(String message){
        System.out.println("[ERROR]\t" + message);
    }
}
