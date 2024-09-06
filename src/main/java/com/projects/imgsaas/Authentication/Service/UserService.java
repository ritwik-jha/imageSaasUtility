package com.projects.imgsaas.Authentication.Service;

import com.projects.imgsaas.Utils.Entity.ResponseVO;
import com.projects.imgsaas.Authentication.Entity.UserDto;

public interface UserService {
    public ResponseVO registerUser(UserDto user);

    public ResponseVO loginUser(UserDto user);
}
