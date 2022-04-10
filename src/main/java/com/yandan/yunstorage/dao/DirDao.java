package com.yandan.yunstorage.dao;

import com.yandan.yunstorage.data.Dir;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Create by yandan
 * 2022/4/9  14:01
 */
@Repository
@Mapper
public interface DirDao {
    @Select("select url,isShare,viewCount,root,shareCode from dir where root=#{root}")
    List<Dir> getDirsByRoot(@Param("root")String root);
    @Select("select url,isShare,viewCount,root,shareCode from dir where url=#{url}")
    Dir getDirByUrl(@Param("url")String url);
    @Select("select url,isShare,viewCount,root,shareCode from dir where url like #{url}")
    List<Dir> getDirLikeUrl(@Param("url")String url);
    @Insert("insert into dir values(#{url},#{isShare},0,#{root},#{shareCode})")
    int addDir(Dir dir);
    @Delete("delete from dir where url=#{url}")
    int deleteByUrl(@Param("url")String url);
    @Delete("delete from dir where root=#{root}")
    int deleteByRoot(@Param("root")String root);
    @Update("update dir set viewCount=#{count} where url=#{url}")
    int updateViewCount(@Param("url")String url,@Param("count")int count);
}
