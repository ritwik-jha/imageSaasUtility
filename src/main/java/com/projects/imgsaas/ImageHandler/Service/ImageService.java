package com.projects.imgsaas.ImageHandler.Service;

import com.projects.imgsaas.Utils.Entity.ResponseVO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public ResponseVO uploadImage(String email, MultipartFile file);
    public ResponseVO getAllImages(String email);
    public ResponseVO getSingleImage(String email, String imageName);
    public ResponseVO deleteSingleImage(String email, String imageName);
}
