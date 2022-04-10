package com.yandan.yunstorage.data;

import lombok.Data;

/**
 * Create by yandan
 * 2022/4/9  13:59
 */
@Data
public class Dir {
    private String url;
    private int isShare;
    private int viewCount;
    private String root;
    private String shareCode;
}
