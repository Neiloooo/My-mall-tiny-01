package com.macro.mall.tiny.service;

import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.mbg.model.UmsRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 刷新Token的功能(类似于续期的感觉?以前只能登录7天现在刷新一下计时,或者说将老Token无效化?)
     * @param oldToken
     * @return
     */
    String refreshToken(String oldToken);


    /**
     * 根据用户名或昵称分页查询用户
     * @param name
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<UmsAdmin> list(String name,Integer pageSize,Integer pageNum);


    /**
     * 根据用户ID获取用户全部信息
     * @param id
     * @return
     */
    UmsAdmin getItem(Long id);


    /**
     * 修改指定用户信息
     * @param id
     * @param admin
     * @return
     */
    int update(Long id,UmsAdmin admin);

    /**
     * 删除用户指定信息
     */
    int delete(Long id);

    /**
     * 修改用户的角色信息
     * @param admin
     * @param roleIds
     * @return
     */
    @Transactional
    int updateRole(Long admin,List<Long> roleIds);


    /**
     * 根据用户ID查询其角色
     * @param adminId
     * @return
     */
    List<UmsRole> getRoleList(Long adminId);


    /**
     * 自定义+-权限
     * 根据用户Id和权限集合,更新权限映射表,自定义+-权限表,因为角色就是权限,所以不需要更新
     * @param permissionId
     * @return
     */
    int updatePermission(Long adminId,List<Long> permissionId);
}
