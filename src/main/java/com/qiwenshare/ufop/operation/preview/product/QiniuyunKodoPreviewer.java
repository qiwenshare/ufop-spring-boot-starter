package com.qiwenshare.ufop.operation.preview.product;

import com.qiniu.util.Auth;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.operation.VideoOperation;
import com.qiwenshare.common.util.HttpsUtils;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@Slf4j
public class QiniuyunKodoPreviewer extends Previewer {


    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoPreviewer(){

    }

    public QiniuyunKodoPreviewer(QiniuyunConfig qiniuyunConfig, ThumbImage thumbImage) {
        this.qiniuyunConfig = qiniuyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    public InputStream getInputStream(String fileUrl) {

        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileUrl, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String publicUrl = String.format("%s/%s", qiniuyunConfig.getKodo().getDomain(), encodedFileName);
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String urlString = auth.privateDownloadUrl(publicUrl, expireInSeconds);

        return HttpsUtils.doGet(urlString);
    }


}
