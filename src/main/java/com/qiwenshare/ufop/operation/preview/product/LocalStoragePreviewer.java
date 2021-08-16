package com.qiwenshare.ufop.operation.preview.product;

import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.UFOPUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class LocalStoragePreviewer extends Previewer {
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
                int thumbImageWidth = UFOPAutoConfiguration.thumbImageWidth;
                int thumbImageHeight = UFOPAutoConfiguration.thumbImageHeight;
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



//        String extendName = UFOPUtils.getFileExtendName(previewFile.getFileUrl());
//        previewFile.setFileUrl(previewFile.getFileUrl().replace("." + extendName, "_min." + extendName));
//        BufferedInputStream bis = null;
//        byte[] buffer = new byte[1024];
//        //设置文件路径
//        File file = FileOperation.newFile(UFOPUtils.getStaticPath() + previewFile.getFileUrl());
//        if (file.exists()) {
//
//            FileInputStream fis = null;
//
//            try {
//                fis = new FileInputStream(file);
//                bis = new BufferedInputStream(fis);
//                OutputStream os = httpServletResponse.getOutputStream();
//                int i = bis.read(buffer);
//                while (i != -1) {
//                    os.write(buffer, 0, i);
//                    i = bis.read(buffer);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (bis != null) {
//                    try {
//                        bis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    @Override
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        //设置文件路径
        File file = UFOPUtils.getLocalSaveFile(previewFile.getFileUrl());
        if (file.exists()) {

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
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
