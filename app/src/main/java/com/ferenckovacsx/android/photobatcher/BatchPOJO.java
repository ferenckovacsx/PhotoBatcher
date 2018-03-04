package com.ferenckovacsx.android.photobatcher;

/**
 * Created by ferenckovacsx on 2018-03-04.
 */

public class BatchPOJO {

    String batchID;
    String uploadDate;
    String lastModifiedDate;
    String note;
    int imageCount;

    public BatchPOJO() {
    }

    public BatchPOJO(String batchID, String uploadDate, String lastModifiedDate, String note, int imageCount) {
        this.batchID = batchID;
        this.uploadDate = uploadDate;
        this.lastModifiedDate = lastModifiedDate;
        this.note = note;
        this.imageCount = imageCount;
    }

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
