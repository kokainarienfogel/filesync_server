package CEKT_FileSync.fs;

import io.atlassian.fugue.Option;

import java.io.File;
import java.util.Arrays;

// Watcher builds the tree on start and in a later version will refresh on changes to the underlying directory

public class Watcher {
    public Option<Folder> getTree(String path) {

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
                folder.addChildren(file);
            });

            return Option.some(folder);
        } catch (Exception ex) {
            return Option.none();
        }
    }
}
