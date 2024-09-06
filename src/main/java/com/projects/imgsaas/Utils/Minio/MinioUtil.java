// package com.projects.imgsaas.Utils.Minio;

// import com.projects.imgsaas.Utils.Entity.ResponseVO;
// import io.minio.*;
// import io.minio.messages.Item;

// import java.util.ArrayList;

// public class MinioUtil {

//     MinioClient minioClient;

//     public MinioUtil(){
//         minioClient = MinioClient.builder().endpoint("").credentials("", "").build();
//     }

//     public ResponseVO uploadObject(String bucketName, String objectName, String pathToFile){
//         ResponseVO res = new ResponseVO(false, "error", null);
//         try{
//             if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())){
//                 minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//             }
//             ObjectWriteResponse objUploadRes =
//                     minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).object(objectName).filename(pathToFile).build());

//             res.setMessage(objUploadRes.toString());
//             res.setStatus(true);

//         }
//         catch (Exception e){
//             res.setMessage(e.getMessage());
//         }

//         return res;
//     }

//     public ResponseVO downloadObject(String bucketName, String objectName, String targetFile){
//         ResponseVO res = new ResponseVO(false, "error", null);

//         try{
//             minioClient.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).object(objectName).filename(targetFile).build());
//             res.setStatus(true);
//             res.setMessage("downloaded");
//                    }
//         catch (Exception e){
//             res.setMessage(e.getMessage());
//         }

//         return res;
//     }

//     public ResponseVO listObjects(String bucketName){
//         ResponseVO res = new ResponseVO(false, "error", null);
//         try{
//             Iterable<Result<Item>> objectList =
//                     minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
//             ArrayList<String> finalList = new ArrayList<>();
//             for(Result<Item> result : objectList){
//                 finalList.add(result.get().objectName());
//             }
//             res.setMessage("done");
//             res.setStatus(true);
//             res.setObj(finalList);
//         }
//         catch (Exception e){
//             res.setMessage(e.getMessage());
//         }

//         return res;
//     }
// }
