package ua.nikolay.filesystem;

import java.io.File;
import java.util.concurrent.Callable;

public class IsDirectoryCallable implements Callable<Boolean> {

    private File file;

    public IsDirectoryCallable(File file) {
        this.file = file;
    }

    @Override
    public Boolean call() throws Exception {
        return file.isDirectory();
    }
}
