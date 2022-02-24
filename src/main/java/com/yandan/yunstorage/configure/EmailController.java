package com.yandan.yunstorage.configure;

import com.yandan.yunstorage.VO.ResultVO;
import com.yandan.yunstorage.util.EmailUtil;
import com.yandan.yunstorage.util.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by yandan
 * 2022/2/24  18:14
 */
@RestController
public class EmailController {
    @Autowired
    private EmailUtil emailUtil;

    @PostMapping("/code/send")
    @ResponseBody
    public ResultVO sendCode(@RequestParam("email")String email,@RequestParam("code") String code){
        if(emailUtil.sendCode(code,email)){
            return ResultVOUtil.success("验证码已发送");
        }else {
            return ResultVOUtil.fail(1,"邮箱无效");
        }
    }
}
