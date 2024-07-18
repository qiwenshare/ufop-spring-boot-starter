package com.qiwenshare.ufop.operation.write.product;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;

import java.io.InputStream;

public class TencentCOSWriter extends Writer {

    private TencentConfig tencentConfig;

    public TencentCOSWriter() {

    }

    public TencentCOSWriter(TencentConfig tencentConfig) {
        this.tencentConfig = this.tencentConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);
        cosClient.putObject(tencentConfig.getCos().getBucketName(),
                UFOPUtils.getTencentObjectNameByFileUrl(writeFile.getFileUrl()), inputStream, new ObjectMetadata());
        cosClient.shutdown();
    }


}
