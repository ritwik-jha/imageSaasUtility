package com.projects.imgsaas.Authentication.Controller;

import com.projects.imgsaas.Utils.Entity.ResponseVO;
import com.projects.imgsaas.Authentication.Entity.UserDto;
import com.projects.imgsaas.Authentication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseVO> register(@RequestBody UserDto user){
        if(user.getEmail() == null || user.getEmail().strip() == "" || user.getPassword() == null || user.getPassword().strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "email and password required", null));
        }
        ResponseVO registerRes = userService.registerUser(user);

        if(registerRes.isStatus()){
            return ResponseEntity.ok(registerRes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerRes);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseVO> login(@RequestBody UserDto user){
        if(user.getEmail() == null || user.getEmail().strip() == "" || user.getPassword() == null || user.getPassword().strip() == ""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO(false, "email and password required", null));
        }
        ResponseVO res = userService.loginUser(user);

        if(res.isStatus()){
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
