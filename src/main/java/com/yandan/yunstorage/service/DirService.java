package com.yandan.yunstorage.service;

import com.yandan.yunstorage.data.Dir;

import java.util.List;

/**
 * Create by yandan
 * 2022/4/9  14:32
 */
public interface DirService {
    /**
     * 添加分享目录
      * @param dirs
     * @return
     */
    int addDir(List<Dir> dirs);

    /**
     * 通过目录删除分享目录
     * @param urls
     * @return
     */
    int deleteByUrl(List<String> urls);

    /**
     * 根据目录获取分享目录信息
     * @param url
     * @return
     */
    Dir getByUrl(String url);

    /**
     * 根据所有者获取分享目录
     * @param root
     * @return
     */
    List<Dir> getByRoot(String root);

    /**
     * 根据url更新访问次数
     * @param url
     * @return
     */
    int updateViewCount(String url);

    /**
     * 开头匹配
     * @param url
     * @return
     */
    List<Dir> getLikeUrl(String url);
}
