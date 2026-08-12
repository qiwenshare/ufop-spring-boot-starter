package com.qiwenshare.ufop.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.qiwenshare.ufop.result.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class ImageOperation {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    // 缩略图输出最大目标分辨率 1080P
    private static final int MAX_TARGET_W = 1920;
    private static final int MAX_TARGET_H = 1080;

    /**
     * 根据比例生成缩略图
     *
     * @param oriFile  原始图像文件
     * @param destFile 输出缩略图
     * @param ratio    缩放比例 (0~1]
     * @return ImageInfo
     */
    public static ImageInfo thumbnailsImageFile(File oriFile, File destFile, double ratio) {
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("ratio must between (0,1]");
        }

        Mat tempGrayMat = null;
        int originalWidth;
        int originalHeight;
        try {
            tempGrayMat = opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            if (tempGrayMat == null || tempGrayMat.empty()) {
                log.error("read temp gray mat failed, file:{}", oriFile.getAbsolutePath());
                return new ImageInfo();
            }
            originalWidth = tempGrayMat.cols();
            originalHeight = tempGrayMat.rows();
        } finally {
            closeMat(tempGrayMat);
        }

        if (originalWidth <= 0 || originalHeight <= 0) {
            log.error("parse image size fail {}", oriFile);
            return new ImageInfo();
        }

        int destW = (int) Math.round(originalWidth * ratio);
        int destH = (int) Math.round(originalHeight * ratio);
        destW = Math.max(1, destW);
        destH = Math.max(1, destH);

        return thumbnailsImageFile(oriFile, destFile, destW, destH);
    }


    /**
     * 指定宽高生成缩略图，内部会做等比适配
     *
     * @param oriFile    原始文件
     * @param destFile   输出文件
     * @param destWidth  目标宽
     * @param destHeight 目标高
     * @return ImageInfo
     */
    public static ImageInfo thumbnailsImageFile(File oriFile, File destFile, int destWidth, int destHeight) {
        Mat mat = null;
        try {
            mat = readMatWithReduceSampling(oriFile);
            if (mat == null || mat.empty()) {
                log.error("read image failed:{}", oriFile.getAbsolutePath());
                return new ImageInfo();
            }

            int srcW = mat.cols();
            int srcH = mat.rows();

            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageWidth(srcW);
            imageInfo.setImageHeight(srcH);
            imageInfo.setChannels(mat.channels());
            imageInfo.setType(mat.type());

            int depth = mat.depth();
            int bitsPerChannel = parseBitsPerChannel(depth);
            imageInfo.setBitsPerPixel(bitsPerChannel * mat.channels());
            // 推断格式和 MIME 类型
            inferFormatAndMimeType(oriFile, imageInfo);
            Size targetSize = calcKeepAspectSize(srcW, srcH, destWidth, destHeight);
            Mat resizedMat = new Mat();
            try {
                opencv_imgproc.resize(mat, resizedMat, targetSize, 0, 0, opencv_imgproc.INTER_AREA);
                opencv_imgcodecs.imwrite(destFile.getAbsolutePath(), resizedMat);
            } finally {
                closeMat(resizedMat);
            }
            return imageInfo;
        } catch (Exception e) {
            log.error("thumbnailsImageFile error", e);
            return new ImageInfo();
        } finally {
            closeMat(mat);
        }
    }

    /**
     * 快捷生成1080P上限缩略图
     */
    public static ImageInfo thumbnailsImageFileToOneK(File oriFile, File destFile) {
        return thumbnailsImageFile(oriFile, destFile, MAX_TARGET_W, MAX_TARGET_H);
    }


    /**
     * OpenCV读取图片：JPG使用解码期降采样；PNG完整读取；
     * 自动选择 scale(1/2/4/8)，保证解码输出 >= MAX_TARGET_W/H，后续只做缩小resize，禁止放大
     */
    private static Mat readMatWithReduceSampling(File oriFile) {
        int[] realSize = getImageRealSize(oriFile);
        int w = realSize[0];
        int h = realSize[1];
        if (w <= 0 || h <= 0) {
            log.warn("parse image meta fail, fallback full read {}", oriFile);
            return opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        }

        String ext = getFileExtendName(oriFile.getName()).toLowerCase();
        // PNG不支持REDUCED，直接完整读取
        if ("png".equals(ext)) {
            return opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        }

        int scale = 1;
        if ((w / 2 > MAX_TARGET_W) || (h / 2 > MAX_TARGET_H)) {
            scale = 2;
        }
        if ((w / 4 > MAX_TARGET_W) || (h / 4 > MAX_TARGET_H)) {
            scale = 4;
        }
        if ((w / 8 > MAX_TARGET_W) || (h / 8 > MAX_TARGET_H)) {
            scale = 8;
        }

        int readFlag;
        switch (scale) {
            case 8:
                readFlag = opencv_imgcodecs.IMREAD_REDUCED_COLOR_8;
                break;
            case 4:
                readFlag = opencv_imgcodecs.IMREAD_REDUCED_COLOR_4;
                break;
            case 2:
                readFlag = opencv_imgcodecs.IMREAD_REDUCED_COLOR_2;
                break;
            default:
                readFlag = opencv_imgcodecs.IMREAD_UNCHANGED;
                break;
        }
        return opencv_imgcodecs.imread(oriFile.getAbsolutePath(), readFlag);
    }

    /**
     * 获取图片原始宽高
     * 优先JDK ImageReader：仅解析图片文件头部，不解码像素，内存开销极小；
     * 注意：返回的是文件存储像素尺寸，手机拍摄带EXIF旋转的图片宽高不会自动交换；
     * 解析失败自动降级OpenCV IMREAD_GRAYSCALE读取。
     *
     * @param oriFile 图片文件
     * @return int[]{width, height}, 解析失败返回 [0,0]
     */
    private static int[] getImageRealSize(File oriFile) {
        if (oriFile == null || !oriFile.exists() || !oriFile.isFile()) {
            log.warn("getImageRealSize file not exist");
            return new int[]{0, 0};
        }

        ImageInputStream iis = null;
        ImageReader reader = null;
        try {
            iis = ImageIO.createImageInputStream(oriFile);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                reader = readers.next();
                reader.setInput(iis, true, true);
                int w = reader.getWidth(0);
                int h = reader.getHeight(0);
                if (w > 0 && h > 0) {
                    return new int[]{w, h};
                } else {
                    log.warn("ImageReader get invalid size w={},h={}, file={}", w, h, oriFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.warn("ImageReader parse meta failed, file={}, fallback opencv imread", oriFile.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.dispose();
                } catch (Exception ignore) {
                }
            }
            if (iis != null) {
                try {
                    iis.close();
                } catch (Exception ignore) {
                }
            }
        }

        // ---------------- 降级：OpenCV灰度读取兜底 ----------------
        Mat tempGrayMat = null;
        try {
            tempGrayMat = opencv_imgcodecs.imread(oriFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            if (tempGrayMat == null || tempGrayMat.empty()) {
                log.warn("opencv fallback read empty, file={}", oriFile.getAbsolutePath());
                return new int[]{0, 0};
            }
            return new int[]{tempGrayMat.cols(), tempGrayMat.rows()};
        } finally {
            closeMat(tempGrayMat);
        }
    }

    /**
     * 根据opencv depth解析单个通道bit位数
     * @param depth mat.depth() 返回值，如 CV_8U,CV_16U
     * @return bit per channel
     */
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

    /**
     * 计算等比缩放后的size，不拉伸
     */
    private static Size calcKeepAspectSize(int srcW, int srcH, int dstW, int dstH) {
        double scaleW = (double) dstW / srcW;
        double scaleH = (double) dstH / srcH;
        double scale = Math.min(scaleW, scaleH);
        int outW = (int) Math.round(srcW * scale);
        int outH = (int) Math.round(srcH * scale);
        outW = Math.max(1, outW);
        outH = Math.max(1, outH);
        return new Size(outW, outH);
    }

    /**
     * 安全释放Mat资源
     */
    public static void closeMat(Mat mat) {
        if (mat != null) {
            try {
                if (!mat.isNull()) {
                    mat.release();
                }
            } catch (Exception ignored) {
            }
            try {
                mat.close();
            } catch (Exception ignored) {
            }
        }
    }

    // 推断文件格式和 MIME 类型
    private static void inferFormatAndMimeType(File file, ImageInfo imageInfo) {
        String fileName = file.getName().toLowerCase();
        String ext = getFileExtendName(fileName);
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            imageInfo.setFormat("jpg");
            imageInfo.setFormatName("JPEG");
            imageInfo.setMimeType("image/jpeg");
        } else if ("png".equals(ext)) {
            imageInfo.setFormat("png");
            imageInfo.setFormatName("PNG");
            imageInfo.setMimeType("image/png");
        } else if ("gif".equals(ext)) {
            imageInfo.setFormat("gif");
            imageInfo.setFormatName("GIF");
            imageInfo.setMimeType("image/gif");
        } else if ("bmp".equals(ext)) {
            imageInfo.setFormat("bmp");
            imageInfo.setFormatName("BMP");
            imageInfo.setMimeType("image/bmp");
        } else if ("tiff".equals(ext) || "tif".equals(ext)) {
            imageInfo.setFormat("tiff");
            imageInfo.setFormatName("TIFF");
            imageInfo.setMimeType("image/tiff");
        } else if ("webp".equals(ext)) {
            imageInfo.setFormat("webp");
            imageInfo.setFormatName("WebP");
            imageInfo.setMimeType("image/webp");
        } else {
            imageInfo.setFormat(ext);
            imageInfo.setFormatName("unknown");
            imageInfo.setMimeType("application/octet‑stream");
        }
    }


    /**
     * 获取文件后缀小写
     */
    public static String getFileExtendName(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1).toLowerCase();
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