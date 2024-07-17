package com.qiwenshare.ufop.operation.download.product;

import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class LocalStorageDownloader extends Downloader {

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        //设置文件路径
        File file = new File(UFOPUtils.getDataPath() + downloadFile.getFileUrl());

        InputStream inputStream = null;
        byte[] bytes = new byte[0];
        InputStream newInputStream = null;
        RandomAccessFile randowAccessFile = null;
        try {
            if (downloadFile.getRange() != null) {
                randowAccessFile = new RandomAccessFile(file, "r");
                randowAccessFile.seek(downloadFile.getRange().getStart());
                bytes = new byte[downloadFile.getRange().getLength()];
                randowAccessFile.read(bytes);
                newInputStream = new ByteArrayInputStream(bytes);
            } else {
                inputStream = new FileInputStream(file);

                newInputStream = IOUtils.toBufferedInputStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(randowAccessFile);
        }
        return newInputStream;

    }
}
