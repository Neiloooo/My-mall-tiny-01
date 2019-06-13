package com.macro.mall.tiny.dao;

import com.macro.mall.tiny.mbg.model.UmsAdminPermissionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户自定权限Dao接口
 */
public interface UmsAdminPermissionRelationDao {
    /**
     * 批量插入用户增加的权限,插入List
     * @param list
     * @return
     */
    int insertList(@Param("list")List<UmsAdminPermissionRelation> list);
}

