package ua.nikolay.filesystem;

import java.io.File;
import java.util.concurrent.Callable;

public class GetFileListCallable implements Callable<File[]> {
  private File file;

    public GetFileListCallable(File file) {
        this.file = file;
    }

    @Override
    public File[] call() throws Exception {
        if(file.isDirectory()) {
            return file.listFiles();
        } else return null;
    }
}
