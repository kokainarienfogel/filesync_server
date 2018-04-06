package CEKT_FileSync.fs;

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

