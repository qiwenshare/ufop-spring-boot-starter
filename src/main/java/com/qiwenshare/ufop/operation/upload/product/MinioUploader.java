package com.qiwenshare.ufop.operation.upload.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileInfo;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import io.minio.UploadPartResponse;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MinioUploader extends Uploader {

    private MinioConfig minioConfig;
    private MinioClient minioClient;

    @Resource
    CacheService cacheService;

    private static final ObjectMapper objectMapper = new ObjectMapper().disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public MinioUploader(){

    }

    public MinioUploader(MinioConfig minioConfig){
        this.minioConfig = minioConfig;
    }

    public MinioUploader(MinioConfig minioConfig, MinioClient minioClient) {
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {

        CustomMinioClient customMinioClient = new CustomMinioClient( MinioAsyncClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(),  minioConfig.getSecretKey())
                .build());
        Multimap<String, String> headers = HashMultimap.create();
        try {
            UploadFileInfo uploadFileInfo = null;
            String uploadPartRequestJson = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");
            if (uploadPartRequestJson != null) {
                try {
                    uploadFileInfo = objectMapper.readValue(uploadPartRequestJson, UploadFileInfo.class);
                } catch (Exception e) {
                    log.error("Failed to parse UploadFileInfo", e);
                }
            }
            String fileUrl = qiwenMultipartFile.getFileUrl(null);
            if (uploadFileInfo == null) {


                CreateMultipartUploadResponse response = customMinioClient.createMultipartUploadAsync(
                        minioConfig.getBucketName(),
                        null,
                        fileUrl,
                        headers,
                        null
                ).get();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(minioConfig.getBucketName());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(response.result().uploadId());

                try {
                    cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest", objectMapper.writeValueAsString(uploadFileInfo));
                } catch (Exception e) {
                    log.error("Failed to serialize UploadFileInfo", e);
                }

            }


            UploadPartResponse response = customMinioClient.uploadPartAsync(
                    uploadFileInfo.getBucketName(),
                    null,
                    uploadFileInfo.getKey(),
                    qiwenMultipartFile.getUploadInputStream(),
                    qiwenMultipartFile.getSize(),
                    uploadFileInfo.getUploadId(),
                    uploadFile.getChunkNumber(),
                    headers,
                    null
            ).get();

            try {
                log.debug("上传结果：" + objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                log.error("Failed to serialize response", e);
            }


        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileInfo uploadFileInfo = null;
        try {
            uploadFileInfo = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
        } catch (Exception e) {
            log.error("Failed to parse UploadFileInfo", e);
        }

        uploadFileResult.setFileUrl(uploadFileInfo.getKey());
        uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
        uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.MINIO);
        uploadFileResult.setIdentifier(uploadFile.getIdentifier());
        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            completeMultipartUpload(uploadFile);
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags");
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");

            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);

        }
        return uploadFileResult;
    }

    private void completeMultipartUpload(UploadFile uploadFile) {

        UploadFileInfo uploadFileInfo = null;
        try {
            uploadFileInfo = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
        } catch (Exception e) {
            log.error("Failed to parse UploadFileInfo", e);
        }

        CustomMinioClient customMinioClient = new CustomMinioClient( MinioAsyncClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(),  minioConfig.getSecretKey())
                .build());
        Multimap<String, String> headers = HashMultimap.create();
        try {
            ListPartsResponse listPartsResponse = customMinioClient.listPartsAsync(
                    minioConfig.getBucketName(),
                    null,
                    uploadFileInfo.getKey(),
                    uploadFile.getTotalChunks() + 10,
                    0,
                    uploadFileInfo.getUploadId(),
                    headers,
                    null
            ).get();
            Part[] parts = listPartsResponse.result().partList().toArray(new Part[]{});

            customMinioClient.completeMultipartUploadAsync(
                    minioConfig.getBucketName(),
                    null,
                    uploadFileInfo.getKey(),
                    uploadFileInfo.getUploadId(),
                    parts,
                    headers,
                    null
            ).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }


    }


}
