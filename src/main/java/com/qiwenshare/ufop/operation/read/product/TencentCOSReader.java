package com.qiwenshare.ufop.operation.read.product;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.exception.operation.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import com.qiwenshare.ufop.util.ReadFileUtils;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;

public class TencentCOSReader extends Reader {

    private TencentConfig tencentConfig;

    public TencentCOSReader() {

    }

    public TencentCOSReader(TencentConfig tencentConfig) {
        this.tencentConfig = tencentConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = FilenameUtils.getExtension(fileUrl);
        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);
        COSObject ossObject = cosClient.getObject(tencentConfig.getCos().getBucketName(),
                UFOPUtils.getTencentObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        try {
            return ReadFileUtils.getContentByInputStream(fileType, inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            cosClient.shutdown();
        }
    }

    public InputStream getInputStream(String fileUrl) {
        COSClient ossClient = TencentUtils.getCOSClient(tencentConfig);
        COSObject cosObject = ossClient.getObject(tencentConfig.getCos().getBucketName(),
                UFOPUtils.getTencentObjectNameByFileUrl(fileUrl));
        return cosObject.getObjectContent();
    }
}
