package com.ferenckovacsx.android.photobatcher.pojo;

/**
 * Created by ferenckovacsx on 2018-02-23.
 */

public class ImagePOJO {

    public String imagePath;
    public String imageName;
    public boolean isChecked;

    public ImagePOJO(String imagePath, String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }

    public ImagePOJO(String imagePath, String imageName, boolean isChecked) {
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.isChecked = isChecked;
    }

    public ImagePOJO(){

    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void toggleChecked(){
        isChecked = !isChecked;
    }
}
