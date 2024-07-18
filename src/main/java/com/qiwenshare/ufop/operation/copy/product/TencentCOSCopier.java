package com.qiwenshare.ufop.operation.copy.product;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.UUID;

public class TencentCOSCopier extends Copier {

    private TencentConfig tencentConfig;

    public TencentCOSCopier() {

    }

    public TencentCOSCopier(TencentConfig tencentConfig) {
        this.tencentConfig = tencentConfig;
    }

    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);
        try {
            cosClient.putObject(tencentConfig.getCos().getBucketName(), fileUrl, inputStream, new ObjectMetadata());
        } finally {
            IOUtils.closeQuietly(inputStream);
            cosClient.shutdown();
        }
        return fileUrl;
    }

}
