package com.macro.mall.tiny.service;

import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;

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
}
