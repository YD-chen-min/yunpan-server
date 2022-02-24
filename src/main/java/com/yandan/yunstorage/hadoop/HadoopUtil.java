package com.yandan.yunstorage.hadoop;

import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by yandan
 * 2022/2/24  11:00
 */
@Component
public class HadoopUtil {

        @Autowired
        private MyConfigure myConfigure;
        public static long mbLength = 1048576L;
        public static long gbLength = 1073741824L;
        //active状态的nameNode地址
        public static   String hadoopJmxServerUrl = "";
        public static final String jmxServerUrlFormat = "%s/jmx?qry=%s";
        public static final String nameNodeInfo = "Hadoop:service=NameNode,name=NameNodeInfo";
        public static final String fsNameSystemState = "Hadoop:service=NameNode,name=FSNamesystemState";

        public  HdfsSummary getHdfsSummary(StatefulHttpClient client) throws IOException {
            if (hadoopJmxServerUrl.equals(""))
                hadoopJmxServerUrl=myConfigure.getHadoopJmxServerUrl();
            HdfsSummary hdfsSummary = new HdfsSummary();
            String namenodeUrl = String.format(jmxServerUrlFormat, hadoopJmxServerUrl, nameNodeInfo);
            MonitorMetrics monitorMetrics = client.get(MonitorMetrics.class, namenodeUrl, null, null);
            hdfsSummary.setTotal(doubleFormat(monitorMetrics.getMetricsValue("Total"), gbLength));
            hdfsSummary.setTotalStr(Converter.toSize(Long.parseLong(String.valueOf(monitorMetrics.getMetricsValue("Total")))));
            hdfsSummary.setDfsFree(doubleFormat(monitorMetrics.getMetricsValue("Free"), gbLength));
            hdfsSummary.setDfsFreeStr(Converter.toSize(Long.parseLong(String.valueOf(monitorMetrics.getMetricsValue("Free")))));
            hdfsSummary.setDfsUsed(doubleFormat(monitorMetrics.getMetricsValue("Used"), gbLength));
            hdfsSummary.setDfsUsedStr(Converter.toSize(Long.parseLong(String.valueOf(monitorMetrics.getMetricsValue("Used")))));
            hdfsSummary.setPercentUsed(doubleFormat(monitorMetrics.getMetricsValue("PercentUsed")));
            hdfsSummary.setSafeMode(monitorMetrics.getMetricsValue("Safemode").toString());
            hdfsSummary.setNonDfsUsed(doubleFormat(monitorMetrics.getMetricsValue("NonDfsUsedSpace"), gbLength));
            hdfsSummary.setNonDfsUsedStr(Converter.toSize(Long.parseLong(String.valueOf(monitorMetrics.getMetricsValue("NonDfsUsedSpace")))));
            hdfsSummary.setBlockPoolUsedSpace(doubleFormat(monitorMetrics.getMetricsValue("BlockPoolUsedSpace"), gbLength));
            hdfsSummary.setBlockPoolUsedSpaceStr(Converter.toSize(Long.parseLong(String.valueOf(monitorMetrics.getMetricsValue("BlockPoolUsedSpace")))));
            hdfsSummary.setPercentBlockPoolUsed(doubleFormat(monitorMetrics.getMetricsValue("PercentBlockPoolUsed")));
            hdfsSummary.setPercentRemaining(doubleFormat(monitorMetrics.getMetricsValue("PercentRemaining")));
            hdfsSummary.setTotalBlocks((int) monitorMetrics.getMetricsValue("TotalBlocks"));
            hdfsSummary.setTotalFiles((int) monitorMetrics.getMetricsValue("TotalFiles"));
            hdfsSummary.setMissingBlocks((int) monitorMetrics.getMetricsValue("NumberOfMissingBlocks"));

            String liveNodesJson = monitorMetrics.getMetricsValue("LiveNodes").toString();
            String deadNodesJson = monitorMetrics.getMetricsValue("DeadNodes").toString();
            List<DataNodeInfo> liveNodes = dataNodeInfoReader(liveNodesJson);
            List<DataNodeInfo> deadNodes = dataNodeInfoReader(deadNodesJson);
            hdfsSummary.setLiveDataNodeInfos(liveNodes);
            hdfsSummary.setDeadDataNodeInfos(deadNodes);

            String fsNameSystemStateUrl = String.format(jmxServerUrlFormat, hadoopJmxServerUrl, fsNameSystemState);
            MonitorMetrics hadoopMetrics = client.get(MonitorMetrics.class, fsNameSystemStateUrl, null, null);
            hdfsSummary.setNumLiveDataNodes((int) hadoopMetrics.getMetricsValue("NumLiveDataNodes"));
            hdfsSummary.setNumDeadDataNodes((int) hadoopMetrics.getMetricsValue("NumDeadDataNodes"));
            hdfsSummary.setVolumeFailuresTotal((int) hadoopMetrics.getMetricsValue("VolumeFailuresTotal"));

            return hdfsSummary;
        }

        public  List<DataNodeInfo> dataNodeInfoReader(String jsonData) throws IOException {
            if (hadoopJmxServerUrl.equals(""))
                hadoopJmxServerUrl=myConfigure.getHadoopJmxServerUrl();
            List<DataNodeInfo> dataNodeInfos = new ArrayList<DataNodeInfo>();
            Map<String, Object> nodes = JsonUtil.fromJsonMap(String.class, Object.class, jsonData);
            for (Map.Entry<String, Object> node : nodes.entrySet()) {
                Map<String, Object> info = (HashMap<String, Object>) node.getValue();
                String nodeName = node.getKey().split(":")[0];
                DataNodeInfo dataNodeInfo = new DataNodeInfo();
                dataNodeInfo.setNodeName(nodeName);
                dataNodeInfo.setNodeAddr(info.get("infoAddr").toString().split(":")[0]);
                dataNodeInfo.setLastContact((int) info.get("lastContact"));
                dataNodeInfo.setUsedSpace(doubleFormat(info.get("usedSpace"), gbLength));
                dataNodeInfo.setUsedSpaceStr(Converter.toSize(Long.parseLong(String.valueOf(info.get("usedSpace")))));
                dataNodeInfo.setAdminState(info.get("adminState").toString());
                dataNodeInfo.setNonDfsUsedSpace(doubleFormat(info.get("nonDfsUsedSpace"), gbLength));
                dataNodeInfo.setNonDfsUsedSpaceStr(Converter.toSize(Long.parseLong(String.valueOf(info.get("nonDfsUsedSpace")))));
                dataNodeInfo.setCapacity(doubleFormat(info.get("capacity"), gbLength));
                dataNodeInfo.setCapacityStr(Converter.toSize(Long.parseLong(String.valueOf(info.get("capacity")))));
                dataNodeInfo.setNumBlocks((int) info.get("numBlocks"));
                dataNodeInfo.setRemaining(doubleFormat(info.get("remaining"), gbLength));
                dataNodeInfo.setRemainingStr(Converter.toSize(Long.parseLong(String.valueOf(info.get("remaining")))));
                dataNodeInfo.setBlockPoolUsed(doubleFormat(info.get("blockPoolUsed"), gbLength));
                dataNodeInfo.setBlockPoolUsedStr(Converter.toSize(Long.parseLong(String.valueOf(info.get("blockPoolUsed")))));
                dataNodeInfo.setBlockPoolUsedPerent(doubleFormat(info.get("blockPoolUsedPercent")));

                dataNodeInfos.add(dataNodeInfo);
            }

            return dataNodeInfos;
        }

        public  DecimalFormat df = new DecimalFormat("######0.00");

        public  double doubleFormat(Object num, long unit) {
            double result = Double.parseDouble(String.valueOf(num)) / unit;
            return Double.parseDouble(df.format(result));
        }

        public  double doubleFormat(Object num) {
            double result = Double.parseDouble(String.valueOf(num));
            return Double.parseDouble(df.format(result));
        }


}
