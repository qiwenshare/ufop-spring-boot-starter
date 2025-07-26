package com.qiwenshare.ufop.operation.upload.product;

import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.operation.UploadException;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;
import com.qiwenshare.ufop.plugins.fastdfs.exception.FdfsServerException;
import com.qiwenshare.ufop.plugins.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.ufop.plugins.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class FastDFSUploader extends Uploader {

    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    CacheService cacheService;

    @Override
    public void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {
        StorePath storePath;

        if (uploadFile.getChunkNumber() <= 1) {
            log.info("上传第一块");

            storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", qiwenMultipartFile.getUploadInputStream(),
                    qiwenMultipartFile.getSize(), qiwenMultipartFile.getExtendName());
            // 记录第一个分片上传的大小
            cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", String.valueOf(qiwenMultipartFile.getSize()), 1000 * 60 * 60);

            log.info("第一块上传完成");
            if (storePath == null) {
                cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number", String.valueOf(uploadFile.getChunkNumber()), 1000 * 60 * 60);

                log.info("获取远程文件路径出错");
                throw new UploadException("获取远程文件路径出错");
            }

            cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path", storePath.getPath(), 1000 * 60 * 60);

            log.info("上传文件 result = {}", storePath.getPath());
        } else {
            log.info("正在上传第{}块：" , uploadFile.getChunkNumber());

            String path = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");

            if (path == null) {
                log.error("无法获取已上传服务器文件地址");
                throw new UploadException("无法获取已上传服务器文件地址");
            }

            String uploadedSizeStr = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");
            long alreadySize = Long.parseLong(uploadedSizeStr);

            // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
            defaultAppendFileStorageClient.modifyFile("group1", path, qiwenMultipartFile.getUploadInputStream(),
                    qiwenMultipartFile.getSize(), alreadySize);
            // 记录分片上传的大小
            cacheService.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", String.valueOf(alreadySize + qiwenMultipartFile.getSize()), 1000 * 60 * 60);

        }
    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();

        String path = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
        uploadFileResult.setFileUrl(path);
        uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
        uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.FAST_DFS);
        uploadFileResult.setIdentifier(uploadFile.getIdentifier());

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
            cacheService.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");

            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }
        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        String path = cacheService.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
        try {
            fastFileStorageClient.deleteFile(path.replace("M00", "group1"));
        } catch (FdfsServerException e) {
            log.error(e.getMessage());
        }
    }
}
