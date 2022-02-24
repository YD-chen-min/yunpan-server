package com.yandan.yunstorage.hadoop;

import lombok.Data;

/**
 * Create by yandan
 * 2022/2/24  10:57
 */
@Data
public class DataNodeInfo {


        //datanode的hostname
        private String nodeName;

        //datanode的ip地址
        private String nodeAddr;

        //datanode的上次链接数量
        private int lastContact;

        //datanode上hdfs的已用空间 GB
        private double usedSpace;
        private String usedSpaceStr;

        //datanode的状态
        private String adminState;

        //datanode上非hdfs的空间大小 GB
        private double nonDfsUsedSpace;
        private String nonDfsUsedSpaceStr;

        //datanode上的总空间大小
        private double capacity;
        private String capacityStr;

        //datanode的block
        private int numBlocks;
        private double remaining;
        private String remainingStr;
        private double blockPoolUsed;
        private String blockPoolUsedStr;
        private double blockPoolUsedPerent;



}
