package com.projects.imgsaas.ImageHandler.Service;

import com.projects.imgsaas.Authentication.Entity.Users;
import com.projects.imgsaas.ImageHandler.Entity.Image;
import com.projects.imgsaas.ImageHandler.Entity.ImageDto;
import com.projects.imgsaas.Utils.Entity.ResponseVO;
import com.projects.imgsaas.Utils.S3Util.S3ServiceUtil;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
@Component
public class ImageServiceImpl implements ImageService{


    private final S3ServiceUtil s3ServiceUtil = new S3ServiceUtil();

    private DynamoDbClient getClient(){
        return DynamoDbClient.builder().credentialsProvider(ProfileCredentialsProvider.create("default")).region(Region.AP_SOUTH_1).build();
    }

    public ResponseVO uploadImage(String email, MultipartFile file) {
        log.info("inside uploadimage");
        ResponseVO res = new ResponseVO(false, "error", null);

        try{
            log.info("connecting to dynamodb and checking image name");
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Image> imageTable = enhancedClient.table("ImageTable", TableSchema.fromBean(Image.class));
            DynamoDbTable<Users> userTable = enhancedClient.table("UserTable", TableSchema.fromBean(Users.class));

            Key imageKey = Key.builder().partitionValue(email).sortValue(file.getOriginalFilename()).build();
            Image queryImage = imageTable.getItem(r -> r.key(imageKey));

            if(queryImage != null){
                res.setMessage("image name already in use");
                return res;
            }

            log.info("image name check complete");
            // creating a new dir to store incoming image
            String uploadDir = "C:\\Users\\ritwi\\Downloads\\uploads\\";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            String filePath = uploadDir + file.getOriginalFilename();
            log.info("transfering image to " + filePath);
            File des = new File(filePath);
            // transfering the incoming image to new destination
            file.transferTo(des);
            log.info("image transferred");

            Key userKey = Key.builder().partitionValue(email).build();
            Users loggedUser = userTable.getItem(r -> r.key(userKey));

            // TODO: aws s3 integration here
            // ResponceVO.message will return the uploaded image url from s3
            log.info("going to save iamge to s3");
            ResponseVO s3ObjectUploadResponse = s3ServiceUtil.putObject(file.getOriginalFilename(), loggedUser.getBucketName(), des);

            if(s3ObjectUploadResponse.isStatus()){
                Image recordImage = new Image();
                recordImage.setBucketName(loggedUser.getBucketName());
                recordImage.setImageName(file.getOriginalFilename());
                recordImage.setOwnerEmail(email);
                recordImage.setImageUrl(s3ObjectUploadResponse.getObj().toString());
                imageTable.putItem(recordImage);

                res.setStatus(true);
                res.setMessage("image uploaded : " + s3ObjectUploadResponse.getMessage());
                res.setObj(s3ObjectUploadResponse);
            }
            else{
                res.setMessage("error uploading image: " + s3ObjectUploadResponse.getMessage());
            }
        }
        catch (Exception e){
            log.error("Exception raised form upload image: " + e.getMessage());
            res.setMessage("exception raised from uploadImage: " + e.getMessage());
        }

        return res;
    }

    public ResponseVO getAllImages(String email) {
        ResponseVO res = new ResponseVO(false, "error", null);

        try{
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Image> imageTable = enhancedClient.table("ImageTable", TableSchema.fromBean(Image.class));

            Key key = Key.builder().partitionValue(email).build();
            QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).build();

            Iterator<Image> itr = imageTable.query(queryEnhancedRequest).items().iterator();

            if(itr.hasNext() == false){
                res.setStatus(true);
                res.setMessage("no records");
                return res;
            }

            ImageDto image;
            ArrayList<ImageDto> imageList = new ArrayList<>();
            while(itr.hasNext()){
                image = new ImageDto();
                Image queryImage = itr.next();
                image.setBucketName(queryImage.getBucketName());
                image.setImageName(queryImage.getImageName());
                image.setOwnerEmail(queryImage.getOwnerEmail());
                image.setImageUrl(queryImage.getImageUrl());

                imageList.add(image);
            }

            res.setStatus(true);
            res.setMessage("images retrieved");
            res.setObj(imageList);
        }
        catch (Exception e){
            res.setMessage(e.getMessage());
        }

        return res;
    }

    public ResponseVO getSingleImage(String email, String imageName) {
        ResponseVO res = new ResponseVO(false, "error", null);

        try {
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Image> imageTable = enhancedClient.table("ImageTable", TableSchema.fromBean(Image.class));

            Key key = Key.builder().partitionValue(email).sortValue(imageName).build();

            Image  image = imageTable.getItem(r -> r.key(key));


            if(image == null){
                res.setMessage("no such image");
                return res;
            }

            ImageDto img = new ImageDto();
            img.setBucketName(image.getBucketName());
            img.setImageName(image.getImageName());
            img.setOwnerEmail(image.getOwnerEmail());
            img.setImageUrl(image.getImageUrl());

            res.setStatus(true);
            res.setMessage("image retrieved");
            res.setObj(img);
        }
        catch(Exception e){
            res.setMessage(e.getMessage());
        }

        return res;
    }

    public ResponseVO deleteSingleImage(String email, String imageName){
        ResponseVO res = new ResponseVO(false, "error", null);
        try {
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Image> imageTable = enhancedClient.table("ImageTable", TableSchema.fromBean(Image.class));

            Key key = Key.builder().partitionValue(email).sortValue(imageName).build();

            Image  image = imageTable.getItem(r -> r.key(key));


            if(image == null){
                res.setMessage("no such image");
                return res;
            }

            String bucketName = image.getBucketName();
            ResponseVO s3DeleteRes = s3ServiceUtil.deleteImage(bucketName, imageName);

            if(s3DeleteRes.isStatus()){
                imageTable.deleteItem(r -> r.key(key));
                res.setStatus(true);
                res.setMessage("Image deleted");
                res.setObj(null);
            }
        } catch (Exception e) {
            // TODO: handle exception
            res.setMessage("Exception from deleteSingleImage: " + e.getMessage());
        }

        return res;
    }
}
