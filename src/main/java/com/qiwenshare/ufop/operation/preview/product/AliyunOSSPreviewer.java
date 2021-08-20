package com.qiwenshare.ufop.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.common.util.HttpsUtils;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.AliyunOSS;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {


    private AliyunConfig aliyunConfig;
    private ThumbImage thumbImage;

    public AliyunOSSPreviewer(){

    }

    public AliyunOSSPreviewer(AliyunConfig aliyunConfig, ThumbImage thumbImage) {
        this.aliyunConfig = aliyunConfig;
        this.thumbImage = thumbImage;
    }

    @Override
    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];

        StringBuffer url = new StringBuffer();
        url.append("https://");
        url.append(aliyunConfig.getOss().getBucketName());
        url.append(".");
        url.append(aliyunConfig.getOss().getEndpoint());
        url.append("/" + UFOPUtils.getAliyunObjectNameByFileUrl(previewFile.getFileUrl()));

        Map<String, Object> param = new HashMap<>();
        int thumbImageWidth = thumbImage.getWidth();
        int thumbImageHeight = thumbImage.getHeight();
        int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
        int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;
        param.put("x-oss-process", "image/resize,m_fill,h_"+height+",w_"+width+"/rotate,0");

        InputStream inputStream = null;
        try {
            URL url1 = new URL(url.toString());
            URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(), url1.getPath(), url1.getQuery(), url1.getRef());
            String urlStr = uri.toASCIIString();

            inputStream = HttpsUtils.doGet(urlStr , param);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            bis = new BufferedInputStream(inputStream);
            OutputStream os = httpServletResponse.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        OSS ossClient = createOSSClient(aliyunConfig.getOss());
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(previewFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
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
        ossClient.shutdown();
    }


    public InputStream getInputStream(DownloadFile downloadFile) {
        OSS ossClient = createOSSClient(aliyunConfig.getOss());
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

    public OSS createOSSClient(AliyunOSS aliyunOSS) {
        String endpoint = aliyunOSS.getEndpoint();
        String accessKeyId = aliyunOSS.getAccessKeyId();
        String accessKeySecret = aliyunOSS.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }


}
