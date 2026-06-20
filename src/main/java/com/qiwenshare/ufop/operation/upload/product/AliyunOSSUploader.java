package com.qiwenshare.ufop.operation.upload.product;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileInfo;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.domain.PartETagDTO;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import com.qiwenshare.ufop.util.AliyunUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class AliyunOSSUploader extends Uploader {

    @Resource
    CacheService cacheService;
    @Resource
    private ObjectMapper objectMapper;

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSUploader() {

    }

    public AliyunOSSUploader(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {

        UploadFileInfo uploadFileInfo = null;
            String uploadPartRequestJson = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");
            if (uploadPartRequestJson != null) {
                try {
                    uploadFileInfo = objectMapper.readValue(uploadPartRequestJson, UploadFileInfo.class);
                } catch (Exception e) {
                    log.error("Failed to parse UploadFileInfo", e);
                }
            }
            String fileUrl = qiwenMultipartFile.getFileUrl();
            if (uploadFileInfo == null) {

                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), fileUrl);
                InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
                String uploadId = upresult.getUploadId();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(aliyunConfig.getOss().getBucketName());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(uploadId);

                try {
                    cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest", objectMapper.writeValueAsString(uploadFileInfo));
                } catch (Exception e) {
                    log.error("Failed to serialize UploadFileInfo", e);
                }

            }

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
            uploadPartRequest.setKey(uploadFileInfo.getKey());
            uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
            uploadPartRequest.setInputStream(qiwenMultipartFile.getUploadInputStream());
            uploadPartRequest.setPartSize(qiwenMultipartFile.getSize());
            uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
            try {
                log.debug(objectMapper.writeValueAsString(uploadPartRequest));
            } catch (Exception e) {
                log.error("Failed to serialize uploadPartRequest", e);
            }

            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

            try {
                log.debug("上传结果：" + objectMapper.writeValueAsString(uploadPartResult));
            } catch (Exception e) {
                log.error("Failed to serialize uploadPartResult", e);
            }

            List<PartETagDTO> partETagDTOs = new ArrayList<>();
            if (cacheService.hasKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags")) {
                try {
                    partETagDTOs = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"),
                            new TypeReference<List<PartETagDTO>>() {});
                } catch (Exception e) {
                    log.error("Failed to parse partETags", e);
                }
            }
            PartETag partETag = uploadPartResult.getPartETag();
            partETagDTOs.add(new PartETagDTO(partETag.getPartNumber(), partETag.getPartSize(), partETag.getETag()));
            try {
                cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", objectMapper.writeValueAsString(partETagDTOs));
            } catch (Exception e) {
                log.error("Failed to serialize partETags", e);
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
        uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);
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


    /**
     * 将文件分块进行升序排序并执行文件上传。
     * @param uploadFile 上传信息
     */
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETagDTO> partETagDTOs = new ArrayList<>();
        try {
            partETagDTOs = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"),
                    new TypeReference<List<PartETagDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to parse partETags", e);
        }

        // 将 DTO 转换为 PartETag
        List<PartETag> partETags = new ArrayList<>();
        for (PartETagDTO dto : partETagDTOs) {
            partETags.add(new PartETag(dto.getPartNumber(), dto.getETag()));
        }

        partETags.sort(Comparator.comparingInt(PartETag::getPartNumber));

        UploadFileInfo uploadFileInfo = null;
        try {
            uploadFileInfo = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
        } catch (Exception e) {
            log.error("Failed to parse UploadFileInfo", e);
        }

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        // 完成上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);

    }

    /**
     * 取消上传
     */
    @Override
    public void cancelUpload(UploadFile uploadFile) {

        UploadFileInfo uploadFileInfo = null;
        try {
            uploadFileInfo = objectMapper.readValue(cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
        } catch (Exception e) {
            log.error("Failed to parse UploadFileInfo", e);
        }

        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId());
        ossClient.abortMultipartUpload(abortMultipartUploadRequest);
    }


}
