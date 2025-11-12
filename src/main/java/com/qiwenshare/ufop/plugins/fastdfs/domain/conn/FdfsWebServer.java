package com.qiwenshare.ufop.plugins.fastdfs.domain.conn;

import com.qiwenshare.ufop.plugins.fastdfs.FdfsClientConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 表示文件Web服务器对象
 * 由Nginx服务器承担此角色，通常配置以后就不会再改变
 *
 * @author tobato
 */
@Component
@ConfigurationProperties(prefix = FdfsClientConstants.ROOT_CONFIG_PREFIX)
public class FdfsWebServer {

    private String webServerUrl;

    public String getWebServerUrl() {
        return webServerUrl;
    }

    public void setWebServerUrl(String webServerUrl) {
        this.webServerUrl = webServerUrl;
    }

}
