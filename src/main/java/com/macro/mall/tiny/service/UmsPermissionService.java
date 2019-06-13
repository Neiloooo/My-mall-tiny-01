package com.macro.mall.tiny.service;
import com.macro.mall.tiny.mbg.model.UmsPermission;

import java.util.List;

/**
 * 后台用户权限管理service(接口)
 */
public interface UmsPermissionService {

    /**
     * 后台添加新的用户权限
     * @param permission
     * @return
     */
    int create(UmsPermission permission);

    /**
     * 修改权限,根据权限Id修改权限,单表修改
     * 修改权限这里要需要全部参数,所有选项都要修改的
     */
    int update(Long id,UmsPermission permission);

    /**
     * 批量删除权限
     */
    int delet(List<Long> ids);

    /**
     * 获取所有权限
     * @return
     */
    List<UmsPermission> getlist();

}
