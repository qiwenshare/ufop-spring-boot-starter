package com.qiwenshare.ufop.operation.preview.product;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TencentCOSPreviewer extends Previewer {


    private TencentConfig tencentConfig;

    public TencentCOSPreviewer() {

    }

    public TencentCOSPreviewer(TencentConfig tencentConfig, ThumbImage thumbImage) {
        this.tencentConfig = tencentConfig;
        setThumbImage(thumbImage);
    }


    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);
        COSObject cosObject = cosClient.getObject(tencentConfig.getCos().getBucketName(),
                UFOPUtils.getTencentObjectNameByFileUrl(previewFile.getFileUrl()));
        InputStream inputStream = cosObject.getObjectContent();
        previewFile.setCosClient(cosClient);
        return inputStream;
    }

}
