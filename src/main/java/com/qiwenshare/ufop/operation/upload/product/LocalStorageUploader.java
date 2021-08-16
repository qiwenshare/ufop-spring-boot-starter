package com.qiwenshare.ufop.operation.upload.product;

import com.qiwenshare.common.exception.NotSameFileExpection;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.operation.upload.request.QiwenMultipartFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class LocalStorageUploader extends Uploader {

    @Override
    public List<UploadFileResult> upload(HttpServletRequest httpServletRequest, UploadFile uploadFile) {
        List<UploadFileResult> uploadFileResultList = new ArrayList<UploadFileResult>();
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new UploadException("未包含文件上传域");
        }

        try {

            Iterator<String> iter = request.getFileNames();
            while (iter.hasNext()) {
                MultipartFile multipartFile = request.getFile(iter.next());
                QiwenMultipartFile qiwenMultipartFile = new QiwenMultipartFile(multipartFile);
                UploadFileResult uploadFileResult = doUpload(qiwenMultipartFile, uploadFile);
                uploadFileResultList.add(uploadFileResult);
            }
        } catch (IOException e) {
            throw new UploadException("未包含文件上传域");
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return uploadFileResultList;
    }

    private UploadFileResult doUpload(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException, NotSameFileExpection {
        UploadFileResult uploadFileResult = new UploadFileResult();

        String fileUrl = UFOPUtils.getUploadFileUrl(uploadFile.getIdentifier(), qiwenMultipartFile.getExtendName());
        String tempFileUrl = fileUrl + "_tmp";
        String confFileUrl = fileUrl.replace("." + qiwenMultipartFile.getExtendName(), ".conf");

        File file = new File(UFOPUtils.getStaticPath() + fileUrl);
        File tempFile = new File(UFOPUtils.getStaticPath() + tempFileUrl);
        File confFile = new File(UFOPUtils.getStaticPath() + confFileUrl);

        //第一步 打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        //第二步 打开通道
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
        //第四步 获取分片数据
        byte[] fileData = qiwenMultipartFile.getUploadBytes();
        //第五步 写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名
        boolean isComplete = checkUploadStatus(uploadFile, confFile);
        uploadFileResult.setFileUrl(fileUrl);
        uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
        uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        uploadFileResult.setStorageType(StorageTypeEnum.LOCAL);

        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
        }

        if (isComplete) {
            tempFile.renameTo(file);
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }
        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        // TODO
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {

    }


    public synchronized boolean checkUploadStatus(UploadFile param, File confFile) throws IOException {
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
        //设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        //设置起始偏移量
        confAccessFile.seek(param.getChunkNumber() - 1);
        //将指定的一个字节写入文件中 127，
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();//不关闭会造成无法占用
        //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是127
        for (int i = 0; i < completeStatusList.length; i++) {
            if (completeStatusList[i] != Byte.MAX_VALUE) {
                return false;
            }
        }
        confFile.delete();
        return true;
    }
}
