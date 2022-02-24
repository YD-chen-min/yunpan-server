package com.yandan.yunstorage.util;

import com.yandan.yunstorage.configure.MyConfigure;
import com.yandan.yunstorage.data.Error;
import com.yandan.yunstorage.data.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Create by yandan
 * 2022/2/22  12:59
 */
@Component
public class Logger {
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    @Autowired
    private  MyConfigure myConfigure;
    public  void userLogIn(String user, String msg){
        String logPath=myConfigure.getLog();
        File file=new File(logPath+user+".log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String message=simpleDateFormat.format(new Date());
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter=new BufferedWriter(new FileWriter(file,true));
            bufferedWriter.append(message+"\t"+msg+"\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public  List<History> userLogOut(String user){
        List<History> historyList=new ArrayList<>();
        String logPath=myConfigure.getLog();
        File file=new File(logPath+user+".log");
        if (file.exists()&&file.isFile()){
            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                String line=bufferedReader.readLine();
                while (line!=null){
                    History history=new History();
                    String[] lines=line.split("\t");
                    history.setTime(lines[0]);
                    history.setOption(lines[1]);
                    historyList.add(history);
                    line=bufferedReader.readLine();
                }
                return historyList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public  void adminLogIn(String msg){
        String logPath=myConfigure.getLog();
        File file=new File(logPath+"admin.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String message=simpleDateFormat.format(new Date());
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter=new BufferedWriter(new FileWriter(file,true));
            bufferedWriter.append(message+"\t"+msg+"\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public  List<History> adminLogOut(){
        List<History> historyList=new ArrayList<>();
        String logPath=myConfigure.getLog();
        File file=new File(logPath+"admin.log");
        if (file.exists()&&file.isFile()){
            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                String line=bufferedReader.readLine();
                while (line!=null){
                    History history=new History();
                    String[] lines=line.split("\t");
                    history.setTime(lines[0]);
                    history.setOption(lines[1]);
                    historyList.add(history);
                    line=bufferedReader.readLine();
                }
                return historyList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  void errorLogIn(String message,StackTraceElement[] stackTraces){
        String logPath=myConfigure.getLog();
        File file=new File(logPath+"error.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuilder stringBuilder=new StringBuilder();
        for(StackTraceElement s:stackTraces){
            stringBuilder.append(s.toString()+"<>");
        }
        String time=simpleDateFormat.format(new Date());
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter=new BufferedWriter(new FileWriter(file,true));
            bufferedWriter.append(time+"\t"+message+"\t"+stringBuilder.toString()+"\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public  List<Error> errorLogOut(){
        List<Error> errorList=new ArrayList<>();
        String logPath=myConfigure.getLog();
        File file=new File(logPath+"error.log");
        if (file.exists()&&file.isFile()){
            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                String line=bufferedReader.readLine();
                while (line!=null){
                    Error error=new Error();
                    String[] lines=line.split("\t");
                    error.setTime(lines[0]);
                    error.setMessage(lines[1]);
                    error.setStackTrace(lines[2].replace("<>","\n"));
                    errorList.add(error);
                    line=bufferedReader.readLine();
                }
                return errorList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
