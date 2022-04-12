package com.yandan.yunstorage.dao;

import com.yandan.yunstorage.VO.MyFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Create by yandan
 * 2022/1/7  10:42
 */
@Repository
@Mapper
public interface FileDao {
    @Insert("insert into files(url,name,size,lastModifyTime,type,root,rootType,realSize) values(#{myFile.url},#{myFile.name},#{myFile.size},#{myFile.lastModifyTime},#{myFile.type},#{root},#{rootType},#{myFile.realSize}) ")
    int insertFile(@Param("myFile") MyFile myFile,@Param("root")String root,@Param("rootType")String rootType);
    @Select("select url,name,lastModifyTime,size,type,root,rootType,deleted,realSize,isShare,downloadCount,shareCode from files where root=#{root} and rootType=#{rootType} and deleted=0")
    List<MyFile> getFilesByType(@Param("rootType")String rootType,@Param("root")String root);
    @Delete("delete from files where url=#{url}")
    int deleteFiles(@Param("url")String url);
    @Update("update files set name=#{newName},url=#{newUrl} where url=#{oldUrl}")
    int modifyFilesName(@Param("oldUrl")String oldUrl,@Param("newUrl")String newUrl,@Param("newName")String newName);
    @Update("update files set deleted = #{delete}  where url = #{url}")
    int setDelete(@Param("url")String url,@Param("delete")int delete);
    @Select("select url,name,lastModifyTime,size,type,root,rootType,deleted,realSize,isShare,downloadCount,shareCode from files where url=#{url}")
    MyFile getFile(@Param("url")String url);
    @Select("select sum(realSize) from files where url like #{url} ")
    Float getFilesSizeByDir(@Param("url")String url);
    @Select("select downloadCount from files where url=#{url}")
    int getDownCount(@Param("url")String url);

    @Update("update files set downloadCount=#{count} where url=#{url}")
    int updateCount(@Param("url")String ur,@Param("count")int count );
    @Update("update files set isShare=#{share},shareCode=#{shareCode} where url=#{url}")
    int updateIsShare(@Param("url")String url,@Param("share")int share,@Param("shareCode")String shareCode);
    @Select("select url,name,lastModifyTime,size,type,root,rootType,deleted,realSize,isShare,downloadCount,shareCode from files where root=#{root} and isShare=1")
    List<MyFile> getShareFiles(@Param("root")String root);
    @Update("update files set deleted = #{delete}  where url like #{dir}")
    int setDeleteByDir(@Param("dir")String dir,@Param("delete") int delete);
    @Delete("delete from files where url like #{dir}")
    int deleteFilesByDir(@Param("dir")String dir);
    @Delete("delete from files where root=#{root}")
    int deleteByRoot(@Param("root")String root);
    @Select("select url,name,lastModifyTime,size,type,root,rootType,deleted,realSize,isShare,downloadCount,shareCode from files where root=#{root} and name like #{name}")
    List<MyFile> search(@Param("root")String root,@Param("name")String name);
}
