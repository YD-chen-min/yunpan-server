package com.yandan.yunstorage.VO;

import lombok.Data;

/**
 * Create by yandan
 * 2021/12/31  12:41
 */
@Data
public class MyFile {
    private String name;
    private String lastModifyTime;
    private String size;
    private String type;
    private String url;
    private long realSize;
    private  int isShare;
    private int downloadCount;
    private String shareCode;
}
