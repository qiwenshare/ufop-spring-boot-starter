package com.qiwenshare.ufop.operation.preview.product;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.operation.VideoOperation;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.IOUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;

@Slf4j
@Component
public class FastDFSPreviewer extends Previewer {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public FastDFSPreviewer(){}

    public FastDFSPreviewer(ThumbImage thumbImage) {

        setThumbImage(thumbImage);
    }

    public InputStream getInputStream(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        group = "group1";
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }


}
