package com.projects.imgsaas.Authentication.Entity;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;



@DynamoDbBean
public class Users {
    private String bucketName;
    private String email;
    private String password;

    public Users(String bucketName, String email, String password) {
        this.bucketName = bucketName;
        this.email = email;
        this.password = password;
    }
    public Users(){}

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getBucketName(){
        return bucketName;
    }
    public void setBucketName(String newBucketName){
        this.bucketName = newBucketName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
