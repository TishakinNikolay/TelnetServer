package ua.nikolay;

import ua.nikolay.filesystem.ExistFileCallable;
import ua.nikolay.filesystem.GetFileListCallable;
import ua.nikolay.filesystem.GetNewFileCallable;
import ua.nikolay.filesystem.IsDirectoryCallable;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SearchEngine {

    private ExecutorService fileSystemExecutor;
    private String rootPath;
    private OutputStreamWriter out;

    public SearchEngine(ExecutorService fileSystemExecutor, String path, OutputStream output) {
        this.fileSystemExecutor = fileSystemExecutor;
        this.rootPath = path;
        this.out = new OutputStreamWriter(output);
    }

    public void printFilesAt(int depth, String mask)
            throws IOException, ExecutionException, InterruptedException {

        Future<File> futureRootPath = fileSystemExecutor.submit(new GetNewFileCallable(rootPath));
        File rootPath = futureRootPath.get();

        Future<Boolean> exists = fileSystemExecutor.submit(new ExistFileCallable(rootPath));
        if(exists.get() == false) {
            throw new FileNotFoundException("There is no such directory" + rootPath.getPath());
        }
        Future<Boolean> isDirectory = fileSystemExecutor.submit(new IsDirectoryCallable(rootPath));
        if (isDirectory.get() == false){
            throw new IllegalArgumentException(rootPath.getPath() + " not a directory");
        }

        if(depth < 0) {
            throw  new IllegalArgumentException(
                    "Argument depth is not valid, value must not be negative, value :" + depth);
        }

        find(depth, mask, rootPath);
    }

     //Сам поиск файлов
     private void  find( int depth, String mask, File rootPath) throws ExecutionException, InterruptedException, IOException {

        Queue<SimpleEntry<Integer, File>> pairs = new ArrayDeque<>();// Очередь для будующих директорий
        int currentDepth = 0; //  Начало обхода начинается с нуля
        SimpleEntry<Integer, File> currentPair = new SimpleEntry(currentDepth,rootPath);
        File currentFile = null;
        pairs.add(currentPair);

        do {
            currentPair = pairs.poll();
            currentFile  = currentPair.getValue();
            currentDepth = currentPair.getKey();

            Future<File[]> futureFileList = fileSystemExecutor.submit(new GetFileListCallable(currentFile));

            File[] files = futureFileList.get(); // Список всех файлов в текущей директории
            if(files != null) {

                for (File item : files) {
                    Future<Boolean> isDirectory = fileSystemExecutor.submit(new IsDirectoryCallable(item));
                    if(isDirectory.get() && (currentDepth + 1) <= depth) {

                        Future<File> fileFuture = fileSystemExecutor.submit(new GetNewFileCallable(item.getPath()));
                        pairs.add(new SimpleEntry<Integer, File>(currentDepth + 1, fileFuture.get()));
                    }

                    if(currentDepth == depth &&  (mask == null || item.getName().contains(mask))) {
                        out.write(item.getAbsolutePath() + " \n\r");
                        out.flush();
                    }
                }
            }
        }while (pairs.size() > 0);
    }
}
