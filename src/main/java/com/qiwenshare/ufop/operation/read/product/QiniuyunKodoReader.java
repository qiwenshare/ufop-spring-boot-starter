package com.qiwenshare.ufop.operation.read.product;

import com.qiniu.util.Auth;
import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.exception.operation.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class QiniuyunKodoReader extends Reader {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoReader(){

    }

    public QiniuyunKodoReader(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = FilenameUtils.getExtension(fileUrl);
        InputStream inputStream = null;
        try {
           inputStream = getInputStream(readFile.getFileUrl());
           return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + fileUrl);


        try {
            return this.downloadAsStream(urlString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
