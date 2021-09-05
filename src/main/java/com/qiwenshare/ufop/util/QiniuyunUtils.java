package com.qiwenshare.ufop.util;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiwenshare.ufop.config.QiniuyunConfig;

public class QiniuyunUtils {
    public static Configuration getCfg(QiniuyunConfig qiniuyunConfig) {
        Region region = null;
        if ("huadong".equals(qiniuyunConfig.getKodo().getEndpoint())) {
            region = Region.huadong();
        } else if ("huanan".equals(qiniuyunConfig.getKodo().getEndpoint())) {
            region = Region.huanan();
        } else if ("huabei".equals(qiniuyunConfig.getKodo().getEndpoint())) {
            region = Region.huabei();
        } else if ("beimei".equals(qiniuyunConfig.getKodo().getEndpoint())) {
            region = Region.beimei();
        } else if ("xinjiapo".equals(qiniuyunConfig.getKodo().getEndpoint())
                || "dongnanya".equals(qiniuyunConfig.getKodo().getEndpoint())) {
            region = Region.xinjiapo();
        }
        //构造一个带指定 Region 对象的配置类
        return new Configuration(region);
    }



}
