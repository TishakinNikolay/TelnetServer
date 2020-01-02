package ua.nikolay;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Connection implements Runnable {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private SearchEngine searchEngine;

    private static final String INVALID_ARGUMENTS_MSG =
            "Bad arguments. You have to tell: depth >= 0  mask(optionaly)";

    public Connection(Socket socket, ExecutorService fileSystemService) {
        if(socket != null) {
            this.socket = socket;

            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            searchEngine = new SearchEngine(fileSystemService, Application.rootPath, os);
        }
    }

    @Override
    public void run() {
        String received;
        int depth;
        String mask;

        String words[];
        Writer writer = new PrintWriter(os);

        do {
            mask = null;
            received = null;
            depth = 0;


            received = readWord();
            if (received == null) {
                close();
                return;
            }
            words = received.split(" ");

            if (Utils.isNumber(words[0]) == false || Integer.parseInt(words[0]) < 0) { //Проверка принятой глубины поиска
                try {//В случае некоректной строки отправляем сообщение об этом и переходим на следующую итерацию
                    writer.write(INVALID_ARGUMENTS_MSG);
                    writer.flush();
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            depth = Integer.parseInt(words[0]);
            if(words.length > 1) {
                mask = words[1];
            }
            try {

                searchEngine.printFilesAt(depth, mask);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }while (true);

    }

    private String readWord() {
        Scanner scanner = new Scanner(is);
        if(scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            return null;
        }

    }
    private void close() {
        try {
            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closed client connection");
    }
}
