package ua.nikolay.filesystem;

import java.io.File;
import java.util.concurrent.Callable;

public class GetNewFileCallable implements Callable<File> {
    private String path;

    public GetNewFileCallable(String path) {
        this.path = path;
    }

    @Override
    public File call() throws Exception {
        return new File(path).getAbsoluteFile();
    }
}
