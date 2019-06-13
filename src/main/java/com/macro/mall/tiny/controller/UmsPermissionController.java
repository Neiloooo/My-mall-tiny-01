package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.service.UmsPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台用户权限管理
 */
@Slf4j
@RestController
@Api(tags = "UmsPermissionController", description = "后台用户权限管理")
@RequestMapping("/permission")
public class UmsPermissionController {

    @Autowired
    private UmsPermissionService permissionService;

    @ApiOperation("添加权限")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult create(@RequestBody UmsPermission permission){
        int count = permissionService.create(permission);
        if (count>0){
            return CommonResult.success(count,"添加权限成功");
        }
        return CommonResult.failed("添加权限失败");
    }


    @ApiOperation("修改权限")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult update(@PathVariable Long id,@RequestBody UmsPermission permission){
        int count = permissionService.update(id,permission);
        if (count>0){
            return CommonResult.success(count,"修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 批量删除,使用了自动生成的条件的andIdin的方法
     * @param ids
     * @return
     */
    @ApiOperation("根据id批量删除权限")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult delet(@RequestParam("ids")List<Long> ids){
        int count = permissionService.delet(ids);
        if (count>0){
            return CommonResult.success(count,"批量删除成功");
        }
        return CommonResult.failed("批量删除成功");
    }

    @ApiOperation("获取所有权限列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<UmsPermission>> list(){
        try {
            List<UmsPermission> permissionList = permissionService.getlist();
            return CommonResult.success(permissionList);
        } catch (Exception e) {
            log.error("获取权限列表失败:{}", e.getMessage());
            return CommonResult.failed("获取权限列表失败");
        }
    }

}
