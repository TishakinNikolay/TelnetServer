package ua.nikolay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private int port;
    private ExecutorService executorService;

    public Server(int port) {

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("SERVER IS NOT CREATED ERROR");
        }

        this.port = port;
        executorService  = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        System.out.println("Server started");
        while (true) {
            System.out.println("waiting for accept...");
            Socket socket = null;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("connected!");
            new Thread( new Connection(socket, executorService)).start();
        }
    }
}
