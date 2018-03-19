package ce_kt_file_tool.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class File {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String name;
    private String path;
    private LocalDateTime date;
    private long size;

    public File(){
        this.name = "";
        this.path = "";
        this.size = 0;
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public String toString(){
        return ("ID: " + this.getId()  + "\r\n" +
                "Path: " + this.getPath() + "\r\n" +
                "Name: " + this.getName() + "\r\n" +
                "Date: " + this.getDate() + "\r\n" +
                "Size: " + this.getSize()/1000 + "KB\r\n"
        );
    }
}
