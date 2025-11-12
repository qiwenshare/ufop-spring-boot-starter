package com.qiwenshare.ufop.util;

import com.alibaba.fastjson2.JSON;
import com.qiwenshare.ufop.result.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ImageOperation {

    /**
     * 根据比例生成缩略图
     *
     * @param oriFile 原始图像文件
     * @param destFile 目标图像文件
     * @param ratio 缩放比例 (0 - 1)
     * @return 图像信息
     */
    public static ImageInfo thumbnailsImageFile(File oriFile, File destFile, double ratio) {
        // 验证比例参数的有效性
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("Ratio must be between 0 and 1 (exclusive)");
        }

        // 读取原始图像获取尺寸
        Mat mat = null;
        try {
            mat = opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        } catch (Exception e) {
            log.error("opencv_imgcodecs.imread exception ", e);
            return new ImageInfo();
        }

        if (mat == null || mat.empty()) {
            log.error("Failed to read image: " + oriFile.getAbsolutePath());
            return new ImageInfo();
        }

        // 计算新的尺寸
        int originalWidth = mat.cols();
        int originalHeight = mat.rows();
        closeMat(mat); // 及时释放资源

        int destWidth = (int) (originalWidth * ratio);
        int destHeight = (int) (originalHeight * ratio);

        // 确保最小尺寸为1像素
        destWidth = Math.max(1, destWidth);
        destHeight = Math.max(1, destHeight);

        // 调用现有的缩略图生成方法
        return thumbnailsImageFile(oriFile, destFile, destWidth, destHeight);
    }


    public static ImageInfo thumbnailsImageFile(File oriFile, File destFile, int destWidth, int destHeight) {

        Mat mat = null;
        try {
            mat = opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        } catch (Exception e) {
            log.error("opencv_imgcodecs.imread exception ", e);
        }

        ImageInfo imageInfo = new ImageInfo();
        if (mat == null || mat.empty()) {
            log.error("Failed to read image: " + oriFile.getAbsolutePath());
            return imageInfo;
        }
        int resizeWidth = mat.cols();
        int resizeHeight = mat.rows();
        if (resizeWidth <= 0 || resizeHeight <= 0) {
            log.error("Invalid image dimensions: width={}, height={}", resizeWidth, resizeHeight);
            closeMat(mat);

            return imageInfo;
        }
        imageInfo.setImageHeight(resizeHeight);
        imageInfo.setImageWidth(resizeWidth);
        int channels = mat.channels();
        int type = mat.type();
        imageInfo.setChannels(channels);
        imageInfo.setType(type);

        // 计算像素深度
        int depth = opencv_core.CV_MAT_DEPTH(mat.type());
        int bitsPerChannel = parseBitsPerChannel(depth);
        imageInfo.setBitsPerPixel(bitsPerChannel * mat.channels());

        // 推断格式和 MIME 类型
        inferFormatAndMimeType(oriFile, imageInfo);

        // 设置默认 DPI（示例值）
        imageInfo.setPhysicalWidthDpi(72);
        imageInfo.setPhysicalHeightDpi(72);


        if (resizeWidth > resizeHeight) {


            if ((long) resizeWidth / (long) resizeHeight > 1.83) {
                if (resizeHeight < destHeight) {
                    closeMat(mat);
                    try {
                        if (!oriFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                            FileUtils.copyFile(oriFile, destFile);
                        } else {
                            log.warn("跳过拷贝，源文件和目标文件路径相同: {}", oriFile.getPath());
                        }
                    } catch (IOException e) {
                        log.error("生成缩略图失败：", e);
                    }
                    return imageInfo;
                }

                resizeWidth = (int) (destHeight / ((double) resizeHeight / (double) resizeWidth));
                resizeHeight = destHeight;
            } else {
                if (resizeWidth < destWidth) {
                    closeMat(mat);
                    try {
                        if (!oriFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                            FileUtils.copyFile(oriFile, destFile);
                        } else {
                            log.warn("跳过拷贝，源文件和目标文件路径相同: {}", oriFile.getPath());
                        }
                    } catch (IOException e) {
                        log.error("生成缩略图失败：", e);
                    }
                    return imageInfo;
                }

                resizeHeight = (int) ((double) resizeHeight / (double) resizeWidth * destWidth);
                resizeWidth = destWidth;
            }
        } else {
            int tmp = resizeHeight;
            resizeHeight = resizeWidth;
            resizeWidth = tmp;

            if ((long) resizeWidth / (long) resizeHeight > 1.83) {
                if (resizeHeight < destHeight) {
                    closeMat(mat);
                    try {
                        if (!oriFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                            FileUtils.copyFile(oriFile, destFile);
                        } else {
                            log.warn("跳过拷贝，源文件和目标文件路径相同: {}", oriFile.getPath());
                        }
                    } catch (IOException e) {
                        log.error("生成缩略图失败：", e);
                    }
                    return imageInfo;
                }

                resizeWidth = (int) (destHeight / ((double) resizeHeight / (double) resizeWidth));
                resizeHeight = destHeight;
            } else {
                if (resizeWidth < destWidth) {
                    closeMat(mat);
                    try {
                        if (!oriFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                            FileUtils.copyFile(oriFile, destFile);
                        } else {
                            log.warn("跳过拷贝，源文件和目标文件路径相同: {}", oriFile.getPath());
                        }
                    } catch (IOException e) {
                        log.error("生成缩略图失败：", e);
                    }
                    return imageInfo;
                }

                resizeHeight = (int) ((double) resizeHeight / (double) resizeWidth * destWidth);
                resizeWidth = destWidth;
            }

            int tmp1 = resizeHeight;
            resizeHeight = resizeWidth;
            resizeWidth = tmp1;
        }

        Size size = new Size(resizeWidth, resizeHeight);
        Mat resizedImage = new Mat();
        try {
            opencv_imgproc.resize(mat, resizedImage, size);
        } finally {
            closeMat(mat);
        }
        try {
            opencv_imgcodecs.imwrite(destFile.getAbsolutePath(), resizedImage);
        } finally {
            closeMat(resizedImage);
        }
        log.info("imageInfo : {}", JSON.toJSONString(imageInfo));
        return imageInfo;
    }


    public static ImageInfo thumbnailsImageFileToOneK(File oriFile, File destFile) {
        return thumbnailsImageFile(oriFile, destFile, 1920, 1080);
    }


    public static void closeMat(Mat mat) {
        if (mat == null) {
            return;
        }
        try {
            mat.release();

        } catch (Exception e2) {
        }
        try {

            mat.close();
        } catch (Exception e2) {
        }
    }


    // 解析每个通道的位数
    private static int parseBitsPerChannel(int depth) {
        switch (depth) {
            case opencv_core.CV_8U:
            case opencv_core.CV_8S:
                return 8;
            case opencv_core.CV_16U:
            case opencv_core.CV_16S:
                return 16;
            case opencv_core.CV_32S:
            case opencv_core.CV_32F:
                return 32;
            case opencv_core.CV_64F:
                return 64;
            default:
                return 0;
        }
    }

    // 推断文件格式和 MIME 类型
    private static void inferFormatAndMimeType(File file, ImageInfo imageInfo) {
        String fileName = file.getName().toLowerCase();
        String format = "unknown";
        String mimeType = "application/octet-stream";

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            format = "JPEG";
            mimeType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            format = "PNG";
            mimeType = "image/png";
        } else if (fileName.endsWith(".bmp")) {
            format = "BMP";
            mimeType = "image/bmp";
        } else if (fileName.endsWith(".gif")) {
            format = "GIF";
            mimeType = "image/gif";
        }

        imageInfo.setFormat(format);
        imageInfo.setMimeType(mimeType);
    }


    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getFileExtendName(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    private static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
