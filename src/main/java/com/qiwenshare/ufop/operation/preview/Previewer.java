package com.qiwenshare.ufop.operation.preview;

import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.exception.operation.PreviewException;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Data
public abstract class Previewer {

    public ThumbImage thumbImage;

    protected abstract InputStream getInputStream(PreviewFile previewFile);

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();


        boolean isVideo = UFOPUtils.isVideoFile(FilenameUtils.getExtension(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + FilenameUtils.getExtension(fileUrl), ".jpg");
        }


        File cacheFile = UFOPUtils.getCacheFile(thumbnailImgUrl);

        if (cacheFile.exists()) {
            FileInputStream fis = null;
            OutputStream outputStream = null;
            try {
                fis = new FileInputStream(cacheFile);
                outputStream = httpServletResponse.getOutputStream();
                IOUtils.copy(fis, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(outputStream);
            }

        } else {
            OutputStream outputStream = null;
            InputStream in = null;
            InputStream inputstream = null;
            try {
                inputstream = getInputStream(previewFile);
            } catch (PreviewException previewException) {
                log.error(previewException.getMessage());
                return;
            }

            try {
                outputStream = httpServletResponse.getOutputStream();
                in = ImageOperation.thumbnailsImageForScale(inputstream, cacheFile, 50);
                IOUtils.copy(in, outputStream);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputStream);
                if (previewFile.getOssClient() != null) {
                    previewFile.getOssClient().shutdown();
                }
            }


        }
    }

    @Deprecated
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        preview(httpServletResponse, previewFile);
    }

    public void preview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = null;

        OutputStream outputStream = null;

        try {
            inputStream = getInputStream(previewFile);
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertTxtCharsetToUTF8(bytes, FilenameUtils.getExtension(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            if (previewFile.getOssClient() != null) {
                previewFile.getOssClient().shutdown();
            }
        }
    }

//    public void videoPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
//        String fileUrl = previewFile.getFileUrl();
//
//
//        String thumbnailImgUrl = fileUrl.replace("." + FilenameUtils.getExtension(fileUrl), ".mp4");
//
//        InputStream inputStream = null;
//
//        OutputStream outputStream = null;
//
//        File cacheFile = UFOPUtils.getCacheFile(thumbnailImgUrl);
//        try {
//            if (!cacheFile.exists()) {
//
//                inputStream = getInputStream(previewFile);
//                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
//
//                Frame captured_frame = null;
//                FFmpegFrameRecorder recorder = null;
//
//
//                frameGrabber.start();
//                recorder = new FFmpegFrameRecorder(cacheFile, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
//                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); //avcodec.AV_CODEC_ID_H264  //AV_CODEC_ID_MPEG4
//                recorder.setFormat("mp4");
//                recorder.setFrameRate(frameGrabber.getFrameRate());
//                //recorder.setSampleFormat(frameGrabber.getSampleFormat()); //
//                recorder.setSampleRate(frameGrabber.getSampleRate());
//
//                recorder.setAudioChannels(frameGrabber.getAudioChannels());
//                recorder.setFrameRate(frameGrabber.getFrameRate());
//                recorder.start();
//                while ((captured_frame = frameGrabber.grabFrame()) != null) {
//                    try {
//                        recorder.setTimestamp(frameGrabber.getTimestamp());
//                        recorder.record(captured_frame);
//                    } catch (Exception e) {
//                    }
//                }
//                recorder.stop();
//                recorder.release();
//                frameGrabber.stop();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
