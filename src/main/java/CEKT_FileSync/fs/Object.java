package CEKT_FileSync.fs;

// Overlapping stuff between file and folder

public abstract class Object {

    private final String name;

    protected Object(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String toString();

}

