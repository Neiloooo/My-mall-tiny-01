package com.macro.mall.tiny.dao;

import com.macro.mall.tiny.mbg.model.UmsAdminRoleRelation;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.mbg.model.UmsRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户与角色管理自定义Dao接口,插入到
 */

public interface UmsAdminRoleRelationDao {
    /**
     * 获取用户权限列表
     * @param admin
     * @return
     */
    List<UmsPermission> getPermissionList(@Param("admin")Long admin);

    /**
     * 批量插入数据到用户与角色关系映射表
     */
    int insertList(@Param("adminRoleRelationList") List<UmsAdminRoleRelation> adminRoleRelationList);


    /**
     * 根据用户Id获取用户的所有角色
     */
    List<UmsRole> getRoleList(@Param("adminId")Long adminId);
}
