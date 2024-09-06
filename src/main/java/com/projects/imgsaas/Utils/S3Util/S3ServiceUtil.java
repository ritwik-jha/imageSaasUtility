package com.projects.imgsaas.Utils.S3Util;

import java.net.URL;
import java.util.*;
import java.io.File;


import com.projects.imgsaas.Utils.Entity.ResponseVO;

//import io.minio.messages.CreateBucketConfiguration;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.S3Object;


@Slf4j
public class S3ServiceUtil {
    private final S3Client s3Client;

    public S3ServiceUtil(){
        s3Client = S3Client.builder().credentialsProvider(ProfileCredentialsProvider.create("default")).region(Region.AP_SOUTH_1).build();
    }

    public ResponseVO putObject(String fileName, String bucketName, File file){
        ResponseVO res = new ResponseVO(false, "error", null);
        try {
            // if(!bucketExists(bucketName)){
            //     ResponseVO createBucketResp = createBucket(bucketName);
            //     if(!createBucketResp.isStatus()){
            //         res.setStatus(false);
            //         res.setMessage(createBucketResp.getMessage());
            //         return res;
            //     }
            // }
            String finalPath = bucketName + "/" + fileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket("mytestbucket2313").key(finalPath).acl(ObjectCannedACL.PUBLIC_READ).build();

            PutObjectResponse putResponse = s3Client.putObject(putObjectRequest, file.toPath());
            log.debug(putResponse.toString());

            S3Utilities s3Utilities = S3Utilities.builder().region(Region.AP_SOUTH_1).build();
            GetUrlRequest  getUrlRequest = GetUrlRequest.builder().bucket("mytestbucket2313").key(finalPath).build();

            URL url = s3Utilities.getUrl(getUrlRequest);
            
            res.setStatus(true);
            res.setMessage("success");
            res.setObj(url.toString());

        }
        catch(Exception e){
            log.error("Exception raised from putObject: " + e.getMessage());
            res.setMessage("Exception raised from putObject: " + e.getMessage());
        }

        return res;
    }

    public boolean bucketExists(String bucketName){
        try {
            ListBucketsResponse  listBucketsResponse = s3Client.listBuckets();
            boolean bucketExists = listBucketsResponse.buckets().stream().anyMatch(bucket -> bucket.name().equals(bucketName));

            return bucketExists;

        } catch (Exception e) {
            // TODO: handle exception
            log.error("Exception raised from bucketExists: " + e.getMessage());
            return false;
        }
    }

    public ResponseVO createBucket(String bucketName){
        ResponseVO res = new ResponseVO(false, "error", null);

        try {
            CreateBucketConfiguration createBucketConfiguration = CreateBucketConfiguration.builder().locationConstraint(Region.AP_SOUTH_1.id()).build();

            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(bucketName).createBucketConfiguration(createBucketConfiguration).build();
            CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);

            log.info("create bucket response: " + createBucketResponse.toString());

            String bucketPolicy = "{\n" +
                    "    \"Version\": \"2012-10-17\",\n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Sid\": \"PublicReadGetObject\",\n" +
                    "            \"Effect\": \"Allow\",\n" +
                    "            \"Principal\": \"*\",\n" +
                    "            \"Action\": \"s3:GetObject\",\n" +
                    "            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

            PutBucketPolicyRequest putBucketPolicyRequest = PutBucketPolicyRequest.builder().bucket(bucketName).policy(bucketPolicy).build();
            PutBucketPolicyResponse putBucketPolicyResponse = s3Client.putBucketPolicy(putBucketPolicyRequest);

            log.info(putBucketPolicyResponse.toString());

            res.setMessage("bucket created");
            res.setStatus(true);

        } catch (Exception e) {
            // TODO: handle exception
            log.error("Exception raised from createBucket: " + e.getMessage());
        }

        return res;
    }

    public ResponseVO getAllObjects(String bucketName){
        ResponseVO res = new ResponseVO(false, "error", null);
        List<Map<String, String>> objectUrls = new ArrayList<>();
        String continuationToken = null;
        
        try{
            while (true) {
                ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                        .bucket("mytestbucket2313")
                        .prefix(bucketName)
                        .maxKeys(1000);  // Adjust max keys per request as needed
    
                if (continuationToken != null) {
                    requestBuilder.continuationToken(continuationToken);
                }
    
                ListObjectsV2Request listObjectsRequest = requestBuilder.build();
                ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
    
                for (S3Object listObject : listObjectsResponse.contents()) {
                    String key = listObject.key();
                    
                    // Construct the URL
                    S3Utilities s3Utilities = S3Utilities.builder().region(Region.AP_SOUTH_1).build(); 
                    String url = s3Utilities.getUrl(builder -> builder.bucket(bucketName).key(key)).toString();
                    
                    Map<String, String> map = new HashMap<>();
                    map.put("imageName", key);
                    map.put("imageUrl", url);

                    objectUrls.add(map);
                    
                }
    
                // If there are no more objects to retrieve, break the loop
                if (!listObjectsResponse.isTruncated()) {
                    break;
                }
    
                // Set the continuation token for the next iteration
                continuationToken = listObjectsResponse.nextContinuationToken();

        }

        res.setStatus(true);
        res.setMessage("fetched");
        res.setObj(objectUrls);
    }
        catch(Exception e){
            log.error("Exception raised from getAllObjects: " + e.getMessage());
            res.setMessage("Exception raised from getAllObjects: " + e.getMessage());
        }

        return res;
    }

    public ResponseVO getSingleObject(String imageName, String bucketName){
        ResponseVO res = new ResponseVO(false, "error", null);

        try {
            S3Utilities s3Utilities = S3Utilities.builder().region(Region.AP_SOUTH_1).build(); 
            String url = s3Utilities.getUrl(builder -> builder.bucket("mytestbucket2313").key(bucketName +"/"+ imageName)).toString();

            Map<String, String> map = new HashMap<>();
            map.put("imageName", imageName);
            map.put("imageUrl", url);

            res.setStatus(true);
            res.setMessage("fetched");
            res.setObj(map);
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Exception from getsingleobject", e);
            res.setMessage("Exception from getsingleobject");
        }

        return res;
    }

    public ResponseVO deleteImage(String bucketName, String imageName){
        ResponseVO res = new ResponseVO(false, "error", null);
        try {
            String finalKey = bucketName + "/" + imageName;

            DeleteObjectRequest  deleteObjectRequest = DeleteObjectRequest.builder().bucket("mytestbucket2313").key(finalKey).build();

            DeleteObjectResponse deleteRes = s3Client.deleteObject(deleteObjectRequest);

            res.setMessage("deleted from s3");
            res.setStatus(true);
            res.setObj(deleteRes);

        } catch (Exception e) {
            // TODO: handle exception
            log.error("Exception from deleteimage", e);
            res.setMessage("Exception from s3 deleteImage: " + e.getMessage());
        }

        return res;
    }
}
