package com.yandan.yunstorage.util;


import com.yandan.yunstorage.VO.ResultVO;

/**
 * Create by yandan
 * 2020/11/12  12:18
 */

public class ResultVOUtil {
        public static ResultVO success(Object object){
            ResultVO resultVO=new ResultVO();
            resultVO.setCode(0);
            resultVO.setMsg("请求成功");
            resultVO.setData(object);
            return resultVO;
        }
        public static ResultVO success(String message){
            ResultVO resultVO=new ResultVO();
            resultVO.setCode(0);
            resultVO.setMsg(message);
            resultVO.setData(null);
            return resultVO;
        }
        public static ResultVO fail(Integer code,String message){
            ResultVO resultVO=new ResultVO();
            resultVO.setCode(code);
            resultVO.setMsg(message);
            resultVO.setData(null);
            return resultVO;
        }
}
