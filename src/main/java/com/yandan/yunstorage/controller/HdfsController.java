package com.yandan.yunstorage.controller;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.hadoop.HadoopUtil;
import com.yandan.yunstorage.hadoop.HdfsSummary;
import com.yandan.yunstorage.hadoop.StatefulHttpClient;
import com.yandan.yunstorage.util.Logger;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Create by yandan
 * 2022/2/24  12:55
 */
@RestController
public class HdfsController {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private Logger logger;

    @GetMapping("/hdfs/get")
    @ResponseBody
    public ResultVO getHDFSInfo() {
        StatefulHttpClient client = new StatefulHttpClient(null);
        try {
            HdfsSummary hdfsSummary = hadoopUtil.getHdfsSummary(client);
            return ResultVOUtil.success(hdfsSummary);
        } catch (IOException e) {
            logger.errorLogIn(e.getMessage(),e.getStackTrace());
        }
        return ResultVOUtil.fail(1, "出现问题了");
    }
}
