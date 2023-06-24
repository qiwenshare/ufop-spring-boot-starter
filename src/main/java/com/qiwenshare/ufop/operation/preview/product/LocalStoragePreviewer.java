package com.qiwenshare.ufop.operation.preview.product;

import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.exception.operation.PreviewException;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

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
        InputStream inputStream = null;
        byte[] bytes = new byte[0];
        try {
            inputStream = new FileInputStream(file);
            bytes = IOUtils.toByteArray(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return new ByteArrayInputStream(bytes);

    }
}
