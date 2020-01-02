package ua.nikolay.filesystem;

import java.io.File;
import java.util.concurrent.Callable;

public class ExistFileCallable implements Callable<Boolean> {
    private File file;

    public ExistFileCallable(File file) {
        this.file = file;
    }

    @Override
    public Boolean call() {
        return file.exists();
    }
}
