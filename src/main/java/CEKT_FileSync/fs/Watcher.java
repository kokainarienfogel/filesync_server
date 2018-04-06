package CEKT_FileSync.fs;

import io.atlassian.fugue.Option;

import java.io.File;

public class Watcher {
    public Option<Folder> getTree(String path) throws Exception {

        java.io.File root = new java.io.File(path);
        if (!root.isDirectory()) return Option.none();
        Folder folder = new Folder(root.getName());

        java.io.File[] list = root.listFiles();
        if (list == null) return Option.some(folder);

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                Option<Folder> childFolder = getTree(f.getAbsolutePath());
                if (childFolder.isEmpty()) return Option.none();
                folder.addChildren(childFolder.get());
            }
            else {
                CEKT_FileSync.fs.File file = new CEKT_FileSync.fs.File(f);
                folder.addChildren(file);
            }
        }
        return Option.some(folder);
    }
}
