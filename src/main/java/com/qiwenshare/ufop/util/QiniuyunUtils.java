package com.qiwenshare.ufop.util;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiwenshare.ufop.config.QiniuyunConfig;

public class QiniuyunUtils {

    public static Configuration getCfg(QiniuyunConfig qiniuyunConfig) {

        //  regionId参考: https://developer.qiniu.com/kodo/1671/region-endpoint-fq
        String regionId = qiniuyunConfig.getKodo().getEndpoint();
        Region region = Region.createWithRegionId(regionId);
        return new Configuration(region);
    }
}
