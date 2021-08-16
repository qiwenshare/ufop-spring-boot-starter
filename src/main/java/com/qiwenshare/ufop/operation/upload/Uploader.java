package com.qiwenshare.ufop.operation.upload;

import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import com.qiwenshare.ufop.util.RedisUtil;
import com.qiwenshare.ufop.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public abstract class Uploader {
    @Resource
    RedisLock redisLock;
    @Resource
    RedisUtil redisUtil;

    public static final String ROOT_PATH = "upload";
    public static final String FILE_SEPARATOR = "/";

    /**
     * 普通上传
     * @param httpServletRequest http的request请求
     * @return 文件列表
     */
    public List<UploadFileResult> upload(HttpServletRequest httpServletRequest) {

        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(1);
        uploadFile.setChunkSize(0);
        uploadFile.setTotalChunks(1);
        uploadFile.setIdentifier(UUID.randomUUID().toString());

        List<UploadFileResult> uploadFileResultList = upload(httpServletRequest, uploadFile);

        return uploadFileResultList;
    }

    /**
     * 分片上传
     * @param httpServletRequest http的request请求
     * @param uploadFile 分片上传参数
     * @return 文件列表
     */
    public abstract List<UploadFileResult> upload(HttpServletRequest httpServletRequest, UploadFile uploadFile);

    /**
     * 取消上传
     * @param uploadFile 分片上传参数
     */
    public abstract void cancelUpload(UploadFile uploadFile);


    public void uploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        redisLock.lock(uploadFile.getIdentifier());
        try {

            if (redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number") == null) {
                redisUtil.set(uploadFile.getIdentifier() + "_current_upload_chunk_number", 1, 1000 * 60 * 60);
            }

            String currentUploadChunkNumber = redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number");
            if (uploadFile.getChunkNumber() != Integer.parseInt(currentUploadChunkNumber)) {
                redisLock.unlock(uploadFile.getIdentifier());
                while (redisLock.tryLock(uploadFile.getIdentifier(), 300, TimeUnit.SECONDS)) {
                    if (uploadFile.getChunkNumber() == Integer.parseInt(redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number"))) {
                        break;
                    } else {
                        redisLock.unlock(uploadFile.getIdentifier());
                    }
                }
            }

            log.debug(">>>>>>>>>>开始上传第{}块>>>>>>>>>>", uploadFile.getChunkNumber());
            doUploadFileChunk(qiwenMultipartFile, uploadFile);
            log.debug("第{}块上传成功", uploadFile.getChunkNumber());
        } catch (Exception e) {
            log.error("***********第{}块上传失败，自动重试**********", uploadFile.getChunkNumber());
            redisUtil.set(uploadFile.getIdentifier() + "_current_upload_chunk_number", uploadFile.getChunkNumber(), 1000 * 60 * 60);
            throw new UploadException("更新远程文件出错", e);
        } finally {
            redisLock.unlock(uploadFile.getIdentifier());
        }

    }

    protected abstract void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile)  throws IOException;



}
