package com.qiwenshare.ufop.plugins.fastdfs.service;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.GroupState;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorageNode;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorageNodeInfo;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorageState;

import java.util.List;


/**
 * 目录服务(Tracker)客户端接口
 *
 * @author tobato
 */
public interface TrackerClient {

    /**
     * 获取存储节点 get the StoreStorage Client
     *
     * @return 存储节点
     */
    StorageNode getStoreStorage();

    /**
     * 按组获取存储节点 get the StoreStorage Client by group
     *
     * @param groupName 分组名称
     * @return 存储节点
     */
    StorageNode getStoreStorage(String groupName);

    /**
     * 获取读取存储节点 get the fetchStorage Client by group and filename
     *
     * @param groupName 分组名称
     * @param filename 文件名
     * @return 存储节点信息
     */
    StorageNodeInfo getFetchStorage(String groupName, String filename);

    /**
     * 获取更新节点 get the updateStorage Client by group and filename
     *
     * @param groupName 分组名称
     * @param filename 文件名
     * @return 存储节点信息
     */
    StorageNodeInfo getUpdateStorage(String groupName, String filename);

    /**
     * 获取组状态list groups
     *
     * @return 分组状态列表
     */
    List<GroupState> listGroups();

    /**
     * 按组名获取存储节点状态list storages by groupName
     *
     * @param groupName 分组名称
     * @return 存储节点状态列表
     */
    List<StorageState> listStorages(String groupName);

    /**
     * 获取存储状态 list storages by groupName and storageIpAddr
     *
     * @param groupName 分组名称
     * @param storageIpAddr 存储节点IP地址
     * @return 存储节点状态列表
     */
    List<StorageState> listStorages(String groupName, String storageIpAddr);

    /**
     * 删除存储节点 delete storage from TrackerServer
     *
     * @param groupName 分组名称
     * @param storageIpAddr 存储节点IP地址
     */
    void deleteStorage(String groupName, String storageIpAddr);

}
