package com.qiwenshare.ufop.util;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.domain.TencentCOS;

public class TencentUtils {

    public static COSClient getCOSClient(TencentConfig tencentConfig) {
        COSCredentials cred = new BasicCOSCredentials(tencentConfig.getCos().getAccessKeyId(), tencentConfig.getCos().getAccessKeySecret());
        Region region = new Region(tencentConfig.getCos().getEndpoint());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

}
