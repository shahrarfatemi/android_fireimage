package com.example.myapplication;

public class ImageUpload {
    private String imageName;
    private String imageUri;

    public ImageUpload() {
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ImageUpload(String imageName, String imageUri) {
        this.imageName = imageName;
        this.imageUri = imageUri;
    }
}
