package dswork.android.demo.component.downloadlist.model;

import java.io.Serializable;

/**
 * Created by ole on 15/5/8.
 */
public class FileInfo implements Serializable
{
    private int id;
    private String url;
    private String fileName;
    private long length;
    private long finished;

    public FileInfo() {
        super();
    }

    public FileInfo(int id, String url, String fileName, long length, long finished) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public int getLengthInt(){
        return Integer.valueOf(String.valueOf(length));
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinished() {
        return finished;
    }

    public int getFinishedInt(){
        return Integer.valueOf(String.valueOf(finished));
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}