package CEKT_FileSync.fs;

import io.atlassian.fugue.Option;

import java.io.File;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.toIntExact;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Watcher {

    private AtomicBoolean changedFlag;
    private AtomicReference<Option<Folder>> result;
    private WatchService watcher;
    private int totalCount;
    private AtomicInteger doneCount = new AtomicInteger();

    private int getFileCount(String path) {
        java.io.File root = new java.io.File(path);
        if (!root.isDirectory()) return 0;

        java.io.File[] list = root.listFiles();
        if (list == null) return 0;

        AtomicInteger folderCount = new AtomicInteger();

        folderCount.addAndGet(
            toIntExact(Arrays.stream(list).filter(f -> !f.isDirectory()).count())
        );

        Arrays.stream(list).parallel().filter(File::isDirectory).forEach(d -> {
            folderCount.addAndGet(getFileCount(d.getAbsolutePath()));
        });
        return folderCount.get();
    }

    private Option<Folder> getTree(String path) {
        try {
            java.io.File root = new java.io.File(path);
            if (!root.isDirectory()) return Option.none();
            Folder folder = new Folder(root.getName());

            java.io.File[] list = root.listFiles();
            if (list == null) return Option.some(folder);

            Arrays.stream(list).parallel().filter(File::isDirectory).forEach(d -> {
                Option<Folder> childFolder = getTree(d.getAbsolutePath());
                if (!childFolder.isEmpty()) folder.addChildren(childFolder.get());
            });

            Arrays.stream(list).parallel().filter(f -> !f.isDirectory()).forEach(f -> {
                CEKT_FileSync.fs.File file = new CEKT_FileSync.fs.File(f);
                file.generateHash();
                System.out.println("File hashed " + doneCount.getAndIncrement() + "/" + totalCount);
                folder.addChildren(file);
            });

            return Option.some(folder);
        } catch (Exception ex) {
            return Option.none();
        }
    }

    public void watchPath(String path) {
        doneCount.set(1);
        totalCount = getFileCount(path);
        changedFlag = new AtomicBoolean();
        if (result == null) result = new AtomicReference<>();
        result.set(getTree(path));

        Runnable watchTask = () -> {
            System.out.println("Watcher thread starting");
            try {
                watcher = FileSystems.getDefault().newWatchService();
                Path p = Paths.get(path);
                p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                System.out.println("Watcher thread active");
                while (true) {
                    watcher.take();
                    System.out.println("**");
                    doneCount.set(1);
                    changedFlag.set(true);
                    result.set(getTree(path));
                }
            } catch (Exception ignored) {
            }
        };
        watchTask.run();
    }

    public boolean hasChanged() {
        return changedFlag.get();
    }

    public Option<Folder> retrieve() {
        changedFlag.set(false);
        return result.get();
    }


}
