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
    private ThumbImage thumbImage;

    public QiniuyunKodoPreviewer(){

    }

    public QiniuyunKodoPreviewer(QiniuyunConfig qiniuyunConfig, ThumbImage thumbImage) {
        this.qiniuyunConfig = qiniuyunConfig;
        this.thumbImage = thumbImage;
    }

    @Override
    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = UFOPUtils.isVideoFile(UFOPUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + UFOPUtils.getFileExtendName(fileUrl), ".jpg");
        }


        File saveFile = UFOPUtils.getCacheFile(thumbnailImgUrl);

        if (saveFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            com.qiwenshare.ufop.util.IOUtils.writeInputStreamToResponse(fis, httpServletResponse);

        } else {
            InputStream inputstream = getInputStream(previewFile.getFileUrl());
            InputStream in = null;
            try {
                int thumbImageWidth = thumbImage.getWidth();
                int thumbImageHeight = thumbImage.getHeight();
                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;
                if (isVideo) {
                    in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                } else {
                    in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            com.qiwenshare.ufop.util.IOUtils.writeInputStreamToResponse(in, httpServletResponse);

        }
    }

    @Override
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = getInputStream(previewFile.getFileUrl());
        OutputStream outputStream = null;
        
        try {
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertCharset(bytes, UFOPUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);
           
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

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
