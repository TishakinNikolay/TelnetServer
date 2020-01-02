package ua.nikolay;

public class Application {
    private static int port;
    public static String rootPath;

    public static void main(String[] args) throws IllegalAccessException{
        if(args.length < 2) {
            throw new IllegalAccessException("Not enough arguments. Need port , rootPath");
        }
        port = Integer.valueOf(args[0]);
        rootPath = args[1];

        Server server = new Server(port);
        new Thread(server).start();
    }
}
