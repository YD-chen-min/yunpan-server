package com.yandan.yunstorage.service.impl;

import com.yandan.yunstorage.dao.DirDao;
import com.yandan.yunstorage.data.Dir;
import com.yandan.yunstorage.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by yandan
 * 2022/4/9  14:42
 */
@Service
public class DirServiceImpl implements DirService {
    @Autowired
    private DirDao dirDao;
    @Override
    public int addDir(List<Dir> dirs) {
        int count=0;
        for(Dir dir:dirs){
            count+=dirDao.addDir(dir);
        }
        return count;
    }

    @Override
    public int deleteByUrl(List<String> urls) {
        int count=0;
        for(String url:urls){
            count+=dirDao.deleteByUrl(url);
        }
        return count;
    }

    @Override
    public Dir getByUrl(String url) {
        return dirDao.getDirByUrl(url);
    }

    @Override
    public List<Dir> getByRoot(String root) {
        return dirDao.getDirsByRoot(root);
    }

    @Override
    public int updateViewCount(String url) {
        Dir dir=getByUrl(url);
        if(dir==null)
            return 0;

        return  dirDao.updateViewCount(url,dir.getViewCount()+1);
    }

    @Override
    public List<Dir> getLikeUrl(String url) {
        return dirDao.getDirLikeUrl(url+"%");
    }
}
