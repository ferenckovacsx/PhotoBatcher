package com.ferenckovacsx.android.photobatcher;

/**
 * Created by ferenckovacsx on 2018-02-23.
 */

public class ImageModel {

    String imagePath;
    String imageName;

    public ImageModel(String imagePath, String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }

    public ImageModel(){

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
}
