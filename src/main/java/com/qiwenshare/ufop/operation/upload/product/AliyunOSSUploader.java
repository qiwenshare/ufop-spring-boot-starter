package com.qiwenshare.ufop.operation.upload.product;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import com.qiwenshare.ufop.util.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class AliyunOSSUploader extends Uploader {

    @Resource
    RedisUtil redisUtil;

    @Override
    public List<UploadFileResult> upload(HttpServletRequest httpServletRequest, UploadFile uploadFile) {

        List<UploadFileResult> uploadFileResultList = new ArrayList<UploadFileResult>();

        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) httpServletRequest;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new UploadException("未包含文件上传域");
        }

        Iterator<String> iter = request.getFileNames();
        while (iter.hasNext()) {
            MultipartFile multipartFile = request.getFile(iter.next());
            QiwenMultipartFile qiwenMultipartFile = new QiwenMultipartFile(multipartFile);
            UploadFileResult uploadFileResult = doUpload(qiwenMultipartFile, uploadFile);
            uploadFileResultList.add(uploadFileResult);
        }

        return uploadFileResultList;
    }

    private UploadFileResult doUpload(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();

        try {

            if (uploadFile.getTotalChunks() == 1) {
                uploadFile.setTotalSize(qiwenMultipartFile.getSize());
            }

            uploadFileChunk(qiwenMultipartFile, uploadFile);

            UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("uploadPartRequest:" + uploadFile.getIdentifier()), UploadFileInfo.class);
            uploadFileResult.setFileUrl(uploadFileInfo.getKey());
            uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
            uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            if (uploadFile.getTotalChunks() == 1) {
                uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
            }
            uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);

            if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
                log.info("分片上传完成");
                completeMultipartUpload(uploadFile);

                redisUtil.deleteKey("partETags:" + uploadFile.getIdentifier());
                redisUtil.deleteKey("uploadPartRequest:" + uploadFile.getIdentifier());

                uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
            } else {
                uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);

            }

        } catch (Exception e) {
            log.error("上传出错：" + e);
            throw new UploadException(e);
        }

        return uploadFileResult;
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {
        redisUtil.getIncr(uploadFile.getIdentifier() + "_current_upload_chunk_number");
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("uploadPartRequest:" + uploadFile.getIdentifier()), UploadFileInfo.class);
        String fileUrl = qiwenMultipartFile.getFileUrl();
        if (uploadFileInfo == null) {
            OSS ossClient = getClient();
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName(), fileUrl);
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            String uploadId = upresult.getUploadId();

            uploadFileInfo = new UploadFileInfo();
            uploadFileInfo.setBucketName(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName());
            uploadFileInfo.setKey(fileUrl);
            uploadFileInfo.setUploadId(uploadId);
            redisUtil.set("uploadPartRequest:" + uploadFile.getIdentifier(), JSON.toJSONString(uploadFileInfo));
            ossClient.shutdown();
        }

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
        uploadPartRequest.setKey(uploadFileInfo.getKey());
        uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
        uploadPartRequest.setInputStream(qiwenMultipartFile.getUploadInputStream());
        uploadPartRequest.setPartSize(qiwenMultipartFile.getSize());
        uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
        log.debug(JSON.toJSONString(uploadPartRequest));
        OSS ossClient = getClient();
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

        log.debug("上传结果：" + JSON.toJSONString(uploadPartResult));
        if (redisUtil.hasKey("partETags:" + uploadFile.getIdentifier())) {
            List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("partETags:" + uploadFile.getIdentifier()), PartETag.class);
            partETags.add(uploadPartResult.getPartETag());
            redisUtil.set("partETags:" + uploadFile.getIdentifier(), JSON.toJSONString(partETags));
        } else {
            List<PartETag> partETags = new ArrayList<PartETag>();
            partETags.add(uploadPartResult.getPartETag());
            redisUtil.set("partETags:" + uploadFile.getIdentifier(), JSON.toJSONString(partETags));
        }
        ossClient.shutdown();

    }


    /**
     * 将文件分块进行升序排序并执行文件上传。
     * @param uploadFile 上传信息
     */
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("partETags:" + uploadFile.getIdentifier()), PartETag.class);

        Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("uploadPartRequest:" + uploadFile.getIdentifier()), UploadFileInfo.class);

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        OSS ossClient = getClient();
        // 完成上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();

    }

    /**
     * 取消上传
     */
    @Override
    public void cancelUpload(UploadFile uploadFile) {
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("uploadPartRequest:" + uploadFile.getIdentifier()), UploadFileInfo.class);

        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId());
        getClient().abortMultipartUpload(abortMultipartUploadRequest);
    }

    private synchronized OSS getClient() {
        OSS ossClient = new OSSClientBuilder().build(UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret());
        return ossClient;
    }

    @Data
    public class UploadFileInfo {
        private String bucketName;
        private String key;
        private String uploadId;
    }

}
