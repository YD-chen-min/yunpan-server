package com.yandan.yunstorage.VO;

import lombok.Data;

/**
 * Create by yandan
 * 2020/11/12  12:25
 */
@Data
public class ResultVO<T> {
    private Integer code;
    private String msg;
    private T data;
}
