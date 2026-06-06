package com.qiwenshare.ufop.operation.preview.product;

import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.exception.operation.PreviewException;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;

@Slf4j
public class LocalStoragePreviewer extends Previewer {

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        //设置文件路径
        File file = UFOPUtils.getLocalSaveFile(previewFile.getFileUrl());
        if (!file.exists()) {
            throw new PreviewException("[UFOP] Failed to get the file stream because the file path does not exist! The file path is: "+ file.getAbsolutePath());
        }

        try {
            return Files.newInputStream(file.toPath());
        }  catch (IOException e) {
            throw new PreviewException("读取预览文件失败", e);
        }


    }
}
