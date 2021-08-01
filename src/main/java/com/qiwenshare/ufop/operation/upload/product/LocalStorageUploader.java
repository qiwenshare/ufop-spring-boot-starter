package com.qiwenshare.ufop.operation.upload.product;

import com.qiwenshare.common.exception.NotSameFileExpection;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.util.PathUtil;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class LocalStorageUploader extends Uploader {


    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest);
        if (!isMultipart) {
            throw new UploadException("未包含文件上传域");
        }
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(1);
        uploadFile.setChunkSize(0);

        uploadFile.setTotalChunks(1);
        uploadFile.setIdentifier(UUID.randomUUID().toString());

        try {

            Iterator<String> iter = standardMultipartHttpServletRequest.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUpload(standardMultipartHttpServletRequest, iter, uploadFile);
            }
        } catch (IOException e) {
            throw new UploadException("未包含文件上传域");
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return saveUploadFileList;
    }

    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest, UploadFile uploadFile) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest);
        if (!isMultipart) {
            throw new UploadException("未包含文件上传域");
        }

        try {

            Iterator<String> iter = standardMultipartHttpServletRequest.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUpload(standardMultipartHttpServletRequest, iter, uploadFile);
            }
        } catch (IOException e) {
            throw new UploadException("未包含文件上传域");
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return saveUploadFileList;
    }

    private List<UploadFile> doUpload(StandardMultipartHttpServletRequest standardMultipartHttpServletRequest,  Iterator<String> iter, UploadFile uploadFile) throws IOException, NotSameFileExpection {

        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        MultipartFile multipartfile = standardMultipartHttpServletRequest.getFile(iter.next());

        String originalName = multipartfile.getOriginalFilename();


        String fileName = getFileName(originalName);
        String extendName = FileUtil.getFileExtendName(originalName);
        if (uploadFile.getTotalChunks() == 1) {
            uploadFile.setTotalSize(multipartfile.getSize());
        }
        uploadFile.setFileName(fileName);
        uploadFile.setFileType(extendName);
        String fileUrl = PathUtil.getUploadFileUrl(uploadFile.getIdentifier(), extendName);
        String tempFileUrl = fileUrl + "_tmp";
        String minFileUrl = fileUrl.replace("." + extendName, "_min." + extendName);
        String confFileUrl = fileUrl.replace("." + extendName, ".conf");

        File file = new File(PathUtil.getStaticPath() + fileUrl);
        File tempFile = new File(PathUtil.getStaticPath() + tempFileUrl);
        File minFile = new File(PathUtil.getStaticPath() + minFileUrl);
        File confFile = new File(PathUtil.getStaticPath() + confFileUrl);

        uploadFile.setStorageType(0);
        uploadFile.setUrl(fileUrl);

        //第一步 打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        //第二步 打开通道
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
        //第四步 获取分片数据
        byte[] fileData = multipartfile.getBytes();
        //第五步 写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名
        boolean isComplete = checkUploadStatus(uploadFile, confFile);
        if (isComplete) {

            tempFile.renameTo(file);
//            if (FileUtil.isImageFile(uploadFile.getFileType())){
//                int thumbImageWidth = UFOPAutoConfiguration.thumbImageWidth;
//                int thumbImageHeight = UFOPAutoConfiguration.thumbImageHeight;
//                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
//                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;
//                ImageOperation.thumbnailsImage(file, minFile, width, height);
//            }

            uploadFile.setSuccess(1);
            uploadFile.setMessage("上传成功");
        } else {
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未完成");
        }
        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);

        return saveUploadFileList;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        // TODO
    }

}
