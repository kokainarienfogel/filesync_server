package ce_kt_file_tool.Filesystem;
import ce_kt_file_tool.entity.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileSearcher extends Thread {

    String[] directories;
    List<File> files = new ArrayList<>();
    String exportToFile;
    String filenameRegex;
    String directory;

    public FileSearcher(String[] directory, String fileNameRegex, String export){
        this.directories = directory;
        this.exportToFile = export;
        this.filenameRegex = fileNameRegex;
    }

    public void run() {
        for(String string:directories){
            searchFiles(string, filenameRegex);
        }
        writeListInFile();
    }

    /**
     * Schreibt Zeile in Datei
     *
     * @param fileName Verzeichnis
     */
    private void writeFileNameInFile(String fileName, String file) {
        try {
            Files.write(Paths.get(file), fileName.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Path is not a link");
            e.printStackTrace();
        }
    }

    /**
     * Schreibt Liste in Datei
     *
     */
    public void writeListInFile() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        DateTimeFormatter dtfRevisionDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Path path = Paths.get(exportToFile);
        Path targetPath = Paths.get("C:\\temp\\");
        try {
            if(Files.exists(path)){
                Files.move(path, targetPath.resolve(dtf.format(now) + "_" + path.getFileName()));
            }
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File drawing : files) {
            String line;
            String pathDir;
        }
    }

    /**
     * Sucht in einem Verzeichnis alle Ordner mit bestimmten Bezeichnungsnamen
     * weiters wird in jedem dieser Verzeichnisse alle Dateien mit Filter gesucht
     *
     * @param directory pfad der durchsucht werden soll
     * @param regex   Dateinamenkonvention bsp.:
     * @return List<File> Liste von Zeichnungen
     */
    private void searchFiles(String directory, String regex) {
        List<java.io.File> fileList = new ArrayList<>();
        Path searchPath = Paths.get(directory);

        try (Stream<Path> searchingDirs = Files.find(searchPath, 3,
                // check is path a directory and has the filter in the name
                (path, attrs) -> String.valueOf(path.getFileName()).matches(regex))) {
            searchingDirs.forEach(path -> fileList.add(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        convertFileListToDrawingList(fileList);
    }

    private void convertFileListToDrawingList(List<java.io.File> fileList){
        int i = 0;
        for(java.io.File file: fileList){
            i++;
            File customFile = new File();
            customFile.setPath(file.getParent());
            customFile.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()));
            customFile.setName(file.getName());
            customFile.setSize(file.length());
            files.add(customFile);
            System.out.println(i + "\r\n");
            System.out.println(customFile.toString() + "\r\n");
        }
    }
}
