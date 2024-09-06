package com.projects.imgsaas.ImageHandler.Entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Image {
    
    private String ownerEmail;
    private String imageName;
    private String bucketName;
    private String imageUrl;

    public Image(String email, String name, String bucket, String url){
        this.ownerEmail = email;
        this.imageName = name;
        this.bucketName = bucket;
        this.imageUrl = url;
    }
    public Image(){}

    @DynamoDbPartitionKey
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @DynamoDbSortKey
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
