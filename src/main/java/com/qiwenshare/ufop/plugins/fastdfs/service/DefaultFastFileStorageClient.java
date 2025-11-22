package com.qiwenshare.ufop.plugins.fastdfs.service;

import com.qiwenshare.ufop.plugins.fastdfs.FdfsClientConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.MetaData;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorageNode;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.ThumbImageConfig;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.StorageSetMetadataCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.StorageUploadFileCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.enums.StorageMetadataSetType;
import com.qiwenshare.ufop.plugins.fastdfs.domain.upload.FastFile;
import com.qiwenshare.ufop.plugins.fastdfs.domain.upload.FastImageFile;
import com.qiwenshare.ufop.plugins.fastdfs.exception.FdfsUnsupportImageTypeException;
import com.qiwenshare.ufop.plugins.fastdfs.exception.FdfsUploadImageException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 面向应用的接口实现
 *
 * @author tobato
 */
@Component
public class DefaultFastFileStorageClient extends DefaultGenerateStorageClient implements FastFileStorageClient {

    /**
     * 支持的图片类型
     */
    private static final List<String> SUPPORT_IMAGE_LIST = Arrays.asList(FdfsClientConstants.SUPPORT_IMAGE_TYPE);

    /**
     * 缩略图生成配置
     */
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 上传文件
     */
    @Override
    public StorePath uploadFile(InputStream inputStream, long fileSize,
                                String fileExtName, Set<MetaData> metaDataSet) {
        FastFile fastFile;
        if (null == metaDataSet) {
            fastFile = new FastFile.Builder()
                    .withFile(inputStream, fileSize, fileExtName)
                    .build();
        } else {
            fastFile = new FastFile.Builder()
                    .withFile(inputStream, fileSize, fileExtName)
                    .withMetaData(metaDataSet)
                    .build();
        }
        return uploadFile(fastFile);
    }

    /**
     * 上传图片并且生成缩略图
     */
    @Override
    public StorePath uploadImageAndCrtThumbImage(InputStream inputStream,
                                                 long fileSize,
                                                 String fileExtName,
                                                 Set<MetaData> metaDataSet) {
        FastImageFile fastImageFile;
        if (null == metaDataSet) {
            fastImageFile = new FastImageFile.Builder()
                    .withFile(inputStream, fileSize, fileExtName)
                    .withThumbImage()
                    .build();
        } else {
            fastImageFile = new FastImageFile.Builder()
                    .withFile(inputStream, fileSize, fileExtName)
                    .withMetaData(metaDataSet)
                    .withThumbImage()
                    .build();
        }
        return uploadImage(fastImageFile);
    }

    /**
     * 上传文件
     * 可通过fastFile对象配置
     * 1. 上传图像分组
     * 2. 上传元数据metaDataSet
     * @param fastFile 上传文件对象
     * @return 存储路径
     */
    @Override
    public StorePath uploadFile(FastFile fastFile) {
        Validate.notNull(fastFile.getInputStream(), "上传文件流不能为空");
        Validate.notBlank(fastFile.getFileExtName(), "文件扩展名不能为空");
        // 获取存储节点
        StorageNode client = getStorageNode(fastFile.getGroupName());
        // 上传文件
        return uploadFileAndMetaData(client, fastFile.getInputStream(),
                fastFile.getFileSize(), fastFile.getFileExtName(),
                fastFile.getMetaDataSet());
    }

    /**
     * 上传图片
     * 可通过fastImageFile对象配置
     * 1. 上传图像分组
     * 2. 上传元数据metaDataSet
     * 3. 是否生成缩略图
     *   3.1 根据默认配置生成缩略图
     *   3.2 根据指定尺寸生成缩略图
     *   3.3 根据指定比例生成缩略图
     * @param fastImageFile 上传图片对象
     * @return 存储路径
     */
    @Override
    public StorePath uploadImage(FastImageFile fastImageFile) {
        String fileExtName = fastImageFile.getFileExtName();
        Validate.notNull(fastImageFile.getInputStream(), "上传文件流不能为空");
        Validate.notBlank(fileExtName, "文件扩展名不能为空");
        // 检查是否能处理此类图片
        if (!isSupportImage(fileExtName)) {
            throw new FdfsUnsupportImageTypeException("不支持的图片格式" + fileExtName);
        }
        // 获取存储节点
        StorageNode client = getStorageNode(fastImageFile.getGroupName());
        byte[] bytes = inputStreamToByte(fastImageFile.getInputStream());

        // 上传文件和metaDataSet
        StorePath path = uploadFileAndMetaData(client, new ByteArrayInputStream(bytes),
                fastImageFile.getFileSize(), fileExtName,
                fastImageFile.getMetaDataSet());

        bytes = null;
        return path;
    }


    /**
     * 获取存储Group
     *
     * @param groupName 组名
     * @return 存储节点
     */
    private StorageNode getStorageNode(String groupName) {
        if (null == groupName) {
            return trackerClient.getStoreStorage();
        } else {
            return trackerClient.getStoreStorage(groupName);
        }
    }

    /**
     * 获取byte流
     *
     * @param inputStream 输入流
     * @return byte数组
     */
    private byte[] inputStreamToByte(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            LOGGER.error("image inputStream to byte error", e);
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        }
    }

    /**
     * 检查是否有MetaData
     *
     * @param metaDataSet 元数据集合
     * @return 是否有元数据
     */
    private boolean hasMetaData(Set<MetaData> metaDataSet) {
        return null != metaDataSet && !metaDataSet.isEmpty();
    }

    /**
     * 是否是支持的图片文件
     *
     * @param fileExtName 文件扩展名
     * @return 是否支持
     */
    private boolean isSupportImage(String fileExtName) {
        return SUPPORT_IMAGE_LIST.contains(fileExtName.toUpperCase());
    }

    /**
     * 上传文件和元数据
     *
     * @param client 存储节点
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileExtName 文件扩展名
     * @param metaDataSet 元数据集合
     * @return 存储路径
     */
    private StorePath uploadFileAndMetaData(StorageNode client, InputStream inputStream, long fileSize,
                                            String fileExtName, Set<MetaData> metaDataSet) {
        // 上传文件
        StorageUploadFileCommand command = new StorageUploadFileCommand(client.getStoreIndex(), inputStream,
                fileExtName, fileSize, false);
        StorePath path = fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        if (hasMetaData(metaDataSet)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
                    metaDataSet, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;
    }



    /**
     * 删除文件
     */
    @Override
    public void deleteFile(String filePath) {
        StorePath storePath = StorePath.parseFromUrl(filePath);
        super.deleteFile(storePath.getGroup(), storePath.getPath());
    }

}
