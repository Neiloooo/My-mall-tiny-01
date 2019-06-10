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

@Api
@RestController
@RequestMapping("/admin")
public class UmAdminController {

    @Autowired
    private UmsAdminService adminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("{jwt.tokenHead}")
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
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam,
                              BindingResult result){
        adminService.login
    }


}
