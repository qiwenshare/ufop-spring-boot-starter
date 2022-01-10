package com.qiwenshare.ufop.operation.write.product;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.exception.operation.WriteException;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.QiniuyunUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class QiniuyunKodoWriter extends Writer {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoWriter(){

    }

    public QiniuyunKodoWriter(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {

        qiniuUpload(writeFile.getFileUrl(), inputStream);
    }

    private void qiniuUpload(String fileUrl, InputStream inputStream) {

        Configuration cfg = QiniuyunUtils.getCfg(qiniuyunConfig);
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        String upToken = auth.uploadToken(qiniuyunConfig.getKodo().getBucketName(), fileUrl);

        String localTempDir = UFOPUtils.getStaticPath() + "temp";

        try {
            //设置断点续传文件进度保存目录
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
            Response response = uploadManager.put(inputStream, fileUrl, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info(putRet.key);
            log.info(putRet.hash);
        } catch (QiniuException ex) {
            throw new WriteException("七牛云写文件失败！", ex);
        } catch (IOException e) {
            throw new WriteException("七牛云写文件失败！", e);
        }


    }


}
