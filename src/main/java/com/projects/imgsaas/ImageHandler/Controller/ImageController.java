package com.projects.imgsaas.ImageHandler.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.projects.imgsaas.ImageHandler.Service.ImageService;
import com.projects.imgsaas.ImageHandler.Service.ImageServiceImpl;
import com.projects.imgsaas.Utils.Entity.ResponseVO;
import com.projects.imgsaas.Utils.JWT.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/image")
public class ImageController {

    private ImageService imageService =  new ImageServiceImpl();

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/upload")
    public ResponseEntity<ResponseVO> uploadImage (@RequestParam MultipartFile file, @RequestHeader("Authorization") String authorizationHeader, @RequestParam String email) {
        // details needed from user
        // 1. token 
        // 2. email 
        // 3. file
        if(authorizationHeader == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "No Authorization header", null));
        }
        
        String jwtToken = authorizationHeader.substring(7); 
        if (jwtToken == null || jwtToken.strip() == "") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token not provided", null));
        }

        if(email == null || email.strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "Email id not provided", null));
        }

        if(jwtUtil.isTokenExpired(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token expired, login again", null));
        }

        if(!jwtUtil.validateToken(jwtToken, email)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Authorization failed", null));
        }

        if(file == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "File not provided", null));
        }


        ResponseVO res = imageService.uploadImage(email, file);

        if(res.isStatus()){
            return ResponseEntity.ok(res); 
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ResponseVO> getAllImages(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String email){
        if(authorizationHeader == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "No Authorization header", null));
        }
        
        String jwtToken = authorizationHeader.substring(7); 
        if (jwtToken == null || jwtToken.strip() == "") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token not provided", null));
        }

        if(email == null || email.strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "Email id not provided", null));
        }

        if(jwtUtil.isTokenExpired(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token expired, login again", null));
        }

        if(!jwtUtil.validateToken(jwtToken, email)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Authorization failed", null));
        }

        ResponseVO res = imageService.getAllImages(email);

        if(res.isStatus()){
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @GetMapping("/get/one")
    public ResponseEntity<ResponseVO> getOneImage(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String email, @RequestParam String imageName){
        if(authorizationHeader == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "No Authorization header", null));
        }
        
        String jwtToken = authorizationHeader.substring(7); 
        if (jwtToken == null || jwtToken.strip() == "") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token not provided", null));
        }

        if(email == null || email.strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "Email id not provided", null));
        }

        if(jwtUtil.isTokenExpired(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token expired, login again", null));
        }

        if(!jwtUtil.validateToken(jwtToken, email)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Authorization failed", null));
        }

        ResponseVO res = imageService.getSingleImage(email, imageName);

        if(res.isStatus()){
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @GetMapping("/delete/one")
    public ResponseEntity<ResponseVO> deleteImage(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String email, @RequestParam String imageName){
        if(authorizationHeader == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "No Authorization header", null));
        }
        
        String jwtToken = authorizationHeader.substring(7); 
        if (jwtToken == null || jwtToken.strip() == "") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token not provided", null));
        }

        if(email == null || email.strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "Email id not provided", null));
        }

        if(jwtUtil.isTokenExpired(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Token expired, login again", null));
        }

        if(!jwtUtil.validateToken(jwtToken, email)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseVO(false, "Authorization failed", null));
        }

        ResponseVO res = imageService.deleteSingleImage(email, imageName);

        if(res.isStatus()){
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    
}
