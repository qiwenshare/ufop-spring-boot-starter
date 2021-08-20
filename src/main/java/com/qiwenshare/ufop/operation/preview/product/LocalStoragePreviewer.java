package com.qiwenshare.ufop.operation.preview.product;

import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class LocalStoragePreviewer extends Previewer {

    private ThumbImage thumbImage;

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    @Override
    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String savePath = UFOPUtils.getStaticPath() + "cache" + File.separator + previewFile.getFileUrl();
        File saveFile = new File(savePath);
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        if (saveFile.exists()) {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(saveFile);
                bis = new BufferedInputStream(fis);
                OutputStream os = httpServletResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
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
        } else {
            InputStream inputstream = getInputStream(previewFile.getFileUrl());
            InputStream in = null;
            try {
                int thumbImageWidth = thumbImage.getWidth();
                int thumbImageHeight = thumbImage.getHeight();
                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;
                in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                bis = new BufferedInputStream(in);
                OutputStream os = httpServletResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
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
    }

    @Override
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        //设置文件路径
        File file = UFOPUtils.getLocalSaveFile(previewFile.getFileUrl());
        if (file.exists()) {

            FileInputStream fis = null;
            OutputStream outputStream = null;
            try {
                fis = new FileInputStream(file);
                byte[] bytes = IOUtils.toByteArray(fis);
                bytes = CharsetUtils.convertCharset(bytes,  UFOPUtils.getFileExtendName(previewFile.getFileUrl()));

//                bis = new BufferedInputStream(fis);
                outputStream = httpServletResponse.getOutputStream();
                outputStream.write(bytes);
//                int i = bis.read(buffer);
//                while (i != -1) {
//                    os.write(buffer, 0, i);
//                    i = bis.read(buffer);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
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
    }

    public InputStream getInputStream(String fileUrl) {
        //设置文件路径
        File file = UFOPUtils.getLocalSaveFile(fileUrl);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}
