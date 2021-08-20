//package com.qiwenshare.ufop.operation.upload.product;
//
//import com.qiwenshare.ufop.operation.upload.Uploader;
//import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
//import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
//import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
//import io.minio.MinioClient;
//import io.minio.PutObjectOptions;
//import io.minio.errors.*;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//
//public class MinioUploader  extends Uploader {
//    @Override
//    public void cancelUpload(UploadFile uploadFile) {
//
//    }
//
//    @Override
//    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {
//
//        try {
//            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
//            MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
//            // 检查存储桶是否已经存在
//            boolean isExist = minioClient.bucketExists("asiatrip");
//            if(isExist) {
//                System.out.println("Bucket already exists.");
//            } else {
//                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
//                minioClient.makeBucket("asiatrip");
//            }
//            PutObjectOptions putObjectOptions = new PutObjectOptions(uploadFile.getTotalSize(), uploadFile.getChunkSize());
//            // 使用putObject上传一个文件到存储桶中。
//            minioClient.putObject("bucketName","objectName", qiwenMultipartFile.getUploadInputStream(), putObjectOptions);
//
//        } catch (InvalidEndpointException e) {
//            e.printStackTrace();
//        } catch (RegionConflictException e) {
//            e.printStackTrace();
//        } catch (InvalidBucketNameException e) {
//            e.printStackTrace();
//        } catch (InsufficientDataException e) {
//            e.printStackTrace();
//        } catch (ErrorResponseException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidPortException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidResponseException e) {
//            e.printStackTrace();
//        } catch (XmlParserException e) {
//            e.printStackTrace();
//        } catch (InternalException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
//        return null;
//    }
//}
