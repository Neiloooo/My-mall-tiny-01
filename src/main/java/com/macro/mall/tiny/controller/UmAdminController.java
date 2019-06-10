package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.dto.UmsAdminLoginParam;
import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "UmsAdminController", description = "后台用户管理")
@RestController
@RequestMapping("/admin")
public class UmAdminController {

    @Autowired
    private UmsAdminService adminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 用户注册接口
     * @param umsAdminParam
     * @param result
     * @return
     */
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<UmsAdmin> register(@RequestBody UmsAdminParam umsAdminParam, BindingResult result){
                UmsAdmin umsAdmin=adminService.register(umsAdminParam);
                if (umsAdmin==null){
                    CommonResult.failed();
                }
                return CommonResult.success(umsAdmin);
}

    /**
     * 用户登录并且返回JWT的接口
     * @param umsAdminLoginParam
     * @param result
     * @return
     */
    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam,
                              BindingResult result){
        //登录成功由Springsecurity帮我们生成token
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null){
            //返回的是参数验证失败的代码
            return CommonResult.validateFailed("用户名或密码错误");
        }
        HashMap<String, String> tokenMap = new HashMap<>();
        //这里新建一个Map封装我们的Token和tokenHead
        tokenMap.put("token",token);
        //tokenHead是在yaml中注入进来的
        tokenMap.put("tokenHead",tokenHead);
        return CommonResult.success(tokenMap);
    }


    @ApiOperation(value = "刷新Token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    public  CommonResult refreshToken(HttpServletRequest request){
        //从请求头Authorization中获取JWT的内容
        //后端自定义请求头会有跨域问题,springsecurity可以解决
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        //如果不能刷新Token
        if (refreshToken ==null){
            return CommonResult.failed("无法更新Token");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token",refreshToken);
        tokenMap.put("tokenHead",tokenHead);
        return CommonResult.success(tokenMap);
    }

    public CommonResult getAdminInfo(Principal principal){

    }

}
