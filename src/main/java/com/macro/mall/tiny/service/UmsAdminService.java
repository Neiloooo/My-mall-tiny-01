package com.macro.mall.tiny.service;

import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsPermission;

import java.util.List;

/**
 * 后台管理员的Service
 */
public interface UmsAdminService {

    /**
     * 根据用户名获取后台管理员(用户全部信息)
     */
    UmsAdmin getAdminByUsername(String username);


    /**
     * 注册接口
     * @param umsAdminParam
     * @return
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 获取用户所有权限(包括角色权限和+-权限)
     * @param adminId
     * @return
     */
    List<UmsPermission> getPermissionList(Long adminId);


    /**
     * 登录接口,且登录后需要返回前端token
     * @param username 用户名
     * @param password 密码
     * @return
     */
    String login(String username,String password);
}
