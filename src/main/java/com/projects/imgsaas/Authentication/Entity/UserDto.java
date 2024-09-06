package com.projects.imgsaas.Authentication.Entity;

public class UserDto {
    private String bucketName;
    private String email;
    private String password;

    public UserDto(String bucketName, String email, String password){
        this.bucketName = bucketName;
        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }

    public String getBucketName(){
        return bucketName;
    }

    public void setBucketName(String bucketName){
        this.bucketName = bucketName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
