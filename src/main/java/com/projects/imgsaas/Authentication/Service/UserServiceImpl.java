package com.projects.imgsaas.Authentication.Service;

import com.projects.imgsaas.Utils.Entity.ResponseVO;
import com.projects.imgsaas.Utils.JWT.JwtUtil;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import com.projects.imgsaas.Authentication.Entity.UserDto;
import com.projects.imgsaas.Authentication.Entity.Users;
import com.projects.imgsaas.Authentication.HelperFunctions.NamesGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

    private DynamoDbClient getClient(){
        return DynamoDbClient.builder().credentialsProvider(ProfileCredentialsProvider.create("default")).region(Region.AP_SOUTH_1).build();
    }

    @Autowired
    JwtUtil jwtUtil;

    public ResponseVO registerUser(UserDto user){
        ResponseVO res = new ResponseVO(false, "error", null);

        try {
            String email = user.getEmail();
            String password = user.getPassword();
            if(email == null || email.strip() == "" || password == null || password.strip() == ""){
                res.setMessage("Email and password cannot be empty");
                return res;
            }

            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Users> userTable = enhancedClient.table("UserTable",  TableSchema.fromBean(Users.class));

            Key key = Key.builder().partitionValue(email).build();

            Users currUser = userTable.getItem(r -> r.key(key));

            if(currUser != null){
                res.setMessage("user already exists");
                return res;
            }

            if(user.getBucketName() == null || user.getBucketName().strip() == ""){
                user.setBucketName(NamesGenerator.generateRandomNames());
            }

            Users record = new Users();
            record.setEmail(user.getEmail());
            record.setBucketName(user.getBucketName());
            record.setPassword(user.getPassword());
            userTable.putItem(record);
            res.setMessage("user registered");
            res.setStatus(true);
            res.setObj(record);

        }
        catch(Exception e) {
            res.setMessage("exception raised from registerUser: " + e.getMessage());
        }

        return res;
    }

    public ResponseVO loginUser(UserDto user){
        ResponseVO res = new ResponseVO(false, "error", null);

        try{
            String email = user.getEmail();
            String password = user.getPassword();
            if(email == null || email.strip() == "" || password == null || password.strip() == ""){
                res.setMessage("Email and password cannot be empty");
                return res;
            }

            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(getClient()).build();
            DynamoDbTable<Users> userTable = enhancedClient.table("UserTable",  TableSchema.fromBean(Users.class));

            Key key = Key.builder().partitionValue(email).build();

            Users currUser = userTable.getItem(r -> r.key(key));
            
            if(currUser == null){
                res.setMessage("no such user");
                return res;
            }

            if(!currUser.getPassword().equals(user.getPassword())){
                res.setMessage("wrong password");
                return res;
            }

            String jwtToken = jwtUtil.generateJwtToken(user.getEmail());
            res.setMessage(jwtToken);
            res.setStatus(true);
            res.setObj(currUser);
        }
        catch(Exception e) {
            res.setMessage("Exception raised form loginUser: " + e.toString());
        }

        return res;
    }
}
