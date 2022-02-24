package com.yandan.yunstorage.converter;

import com.yandan.yunstorage.VO.MyFile;
import org.apache.hadoop.fs.FileStatus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Create by yandan
 * 2021/12/31  13:25
 */
public class Converter {
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
    private static DecimalFormat df   = new DecimalFormat("######0.00");
    public static MyFile fileStatus2File(FileStatus fileStatus, String hsdfUrl){
        if(!hsdfUrl.endsWith("/")) hsdfUrl+="/";
        MyFile myFile =new MyFile();
        myFile.setLastModifyTime(String.valueOf(fileStatus.getModificationTime()));
        myFile.setSize(toSize(fileStatus.getLen()));
        myFile.setName(fileStatus.getPath().toString().replace(hsdfUrl,""));
        if (fileStatus.isFile()) {
            if (myFile.getName().contains(".")){
                String[] names= myFile.getName().split("\\.");
                myFile.setType(names[names.length-1]);
            }else{
                myFile.setType("file");
            }
        } else myFile.setType("dir");
        myFile.setUrl(fileStatus.getPath().toString());
        return myFile;
    }
    public static String toSize(double size){
        String sizeStr="";
        int level=0;
        while (size>1024){
            size=size/1024.0;
            level++;
        }
        switch (level){
            case 0: sizeStr="B";break;
            case 1: sizeStr="KB";break;
            case 2: sizeStr="MB";break;
            case 3: sizeStr="GB";break;
            case 4: sizeStr="TB";break;
            case 5: sizeStr="PB";break;
        }

        sizeStr=df.format(size)+sizeStr;
        return sizeStr;
    }
    public static float toGB(long size){
        return size/1024/1024/1024;
    }
}
