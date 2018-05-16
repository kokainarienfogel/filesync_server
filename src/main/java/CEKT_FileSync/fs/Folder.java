package CEKT_FileSync.fs;

import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import java.util.*;

// Folder object, can contain other File/Folder objects - node object for our tree
// We can recursively look through all the folders to retrieve a file

public class Folder extends Object {

    private List<Object> children;

    protected Folder(String name) {
        super(name);
        this.children = new LinkedList<>();
    }

    @Override
    public String toString() {
        String childStr = this.children.stream().map(Object::toString).reduce((x, y) -> x + ", " + y).orElse("<empty>");
        return "(" + super.getName() + ": " + childStr + ")";
    }

    protected void addChildren(Object child) {
        this.children.add(child);
    }

    public Collection<Object> getChildren() {
        return Collections.unmodifiableCollection(this.children);
    }

    public Option<File> getFileByHash(String hash) {
        return Option.fromOptional(
                this.children.parallelStream()
                        .map(c -> c instanceof Folder ? ((Folder) c).getFileByHash(hash): Option.some((File) c))
                        .filter(c -> !c.isEmpty())
                        .map(Maybe::get)
                        .filter(c -> hash.equals(c.getHash()))
                        .findAny()
        );
    }
}
