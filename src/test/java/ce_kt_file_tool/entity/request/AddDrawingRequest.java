package ce_kt_file_tool.entity.request;

import java.time.LocalTime;

public class AddDrawingRequest {

    private String drawingName;
    private String sourcePath;
    private String convertedpath;
    private LocalTime revisionDate;
    private String revision;

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getConvertedpath() {
        return convertedpath;
    }

    public void setConvertedpath(String convertedpath) {
        this.convertedpath = convertedpath;
    }

    public LocalTime getRevisionDate() {
        return revisionDate;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevisionDate(LocalTime revisionDate) {
        this.revisionDate = revisionDate;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
