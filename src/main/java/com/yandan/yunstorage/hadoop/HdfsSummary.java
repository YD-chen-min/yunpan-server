package com.yandan.yunstorage.hadoop;

import lombok.Data;

import java.util.List;

/**
 * Create by yandan
 * 2022/2/24  10:56
 */
@Data
public class HdfsSummary {


        //"name": "Hadoop:service=NameNode,name=NameNodeInfo"
        //总空间大小 GB
        private double total;
        private String totalStr;
        //hdfs已使用的空间大小 GB
        private double dfsUsed;
        private String dfsUsedStr;

        //hdfs已使用空间百分比
        private double percentUsed;

        //hdfs空闲空间 GB
        private double dfsFree;
        private String dfsFreeStr;
        //hdfs是否处于safemode
        private String safeMode;

        //非hdfs空间大小 GB
        private double nonDfsUsed;
        private String nonDfsUsedStr;
        //集群该namespace的hdfs使用容量大小
        private double blockPoolUsedSpace;
        private String blockPoolUsedSpaceStr;
        //集群该namespace的hdfs使用容量所占百分比
        private double percentBlockPoolUsed;
        private double percentRemaining;

        //集群总的block数
        private int totalBlocks;

        //集群总的文件数
        private int totalFiles;

        //集群丢失的block数量
        private int missingBlocks;

        //处于可用状态的datanode汇总
        private List<DataNodeInfo> liveDataNodeInfos;

        //处于不可用状态的datanode汇总
        private List<DataNodeInfo> deadDataNodeInfos;


        //"name": "Hadoop:service=NameNode,name=FSNamesystemState"
        //处于可用状态的datanode数量
        private int numLiveDataNodes;

        //处于不可用状态的datanode数量
        private int numDeadDataNodes;

        //坏盘的数量
        private int volumeFailuresTotal;




}
