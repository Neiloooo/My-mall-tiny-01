package com.macro.mall.tiny.dao;

import com.macro.mall.tiny.mbg.model.UmsPermission;import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户与角色管理自定义Dao接口
 */

public interface UmsAdminRoleRelationDao {

    List<UmsPermission> getPermissionList(@Param("admin")Long admin);
}
