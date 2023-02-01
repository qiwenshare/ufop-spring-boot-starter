package com.qiwenshare.ufop.operation.upload.request;

import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class QiwenMultipartFile {

    MultipartFile multipartFile = null;

    private QiwenMultipartFile() {
    }

    public QiwenMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {
        String originalName = getMultipartFile().getOriginalFilename();
        if (!originalName.contains(".")) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public String getExtendName() {
        String originalName = getMultipartFile().getOriginalFilename();
        return FilenameUtils.getExtension(originalName);
    }

    public String getFileUrl() {
        String uuid = UUID.randomUUID().toString();
        return UFOPUtils.getUploadFileUrl(uuid, getExtendName());
    }

    public String getFileUrl(String identify) {
        return UFOPUtils.getUploadFileUrl(identify, getExtendName());
    }

    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }

    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }

    public long getSize() {
        return getMultipartFile().getSize();
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

}
