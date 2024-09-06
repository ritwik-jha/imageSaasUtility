package com.projects.imgsaas.ImageHandler.Entity;


public class ImageDto {
    private String ownerEmail;
    private String imageName;
    private String bucketName;
    private String imageUrl;

    public ImageDto(String email, String name, String bucket, String url){
        this.ownerEmail = email;
        this.imageName = name;
        this.bucketName = bucket;
        this.imageUrl = url;
    }

    public ImageDto(){}

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String url){
        this.imageUrl = url;
    }
}
