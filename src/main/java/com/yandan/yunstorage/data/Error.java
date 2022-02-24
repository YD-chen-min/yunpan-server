package com.yandan.yunstorage.data;

import lombok.Data;

/**
 * Create by yandan
 * 2022/2/24  16:12
 */
@Data
public class Error {
    private String time;
    private String message;
    private String stackTrace;
}
