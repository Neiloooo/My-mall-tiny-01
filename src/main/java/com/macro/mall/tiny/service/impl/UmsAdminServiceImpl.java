package com.macro.mall.tiny.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.tiny.Utils.JwtTokenUtil;
import com.macro.mall.tiny.dao.UmsAdminRoleRelationDao;
import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.mapper.UmsAdminMapper;
import com.macro.mall.tiny.mbg.mapper.UmsAdminPermissionRelationMapper;
import com.macro.mall.tiny.mbg.mapper.UmsAdminRoleRelationMapper;
import com.macro.mall.tiny.mbg.model.*;
import com.macro.mall.tiny.service.UmsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminPermissionRelationMapper adminPermissionRelationMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //引入springsecurity的加密机制
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UmsAdminRoleRelationDao umsAdminRoleRelationDao;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    /**
     * 根据用户名获取管理员(用户全部信息)
     *
     * @param username
     * @return
     */
    @Override
    public UmsAdmin getAdminByUsername(String username) {
        //根据用户名查询用户全部信息
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(example);
        //如果数据库中有这个用户,将用户返回
        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }
        return null;
    }

    /**
     * 注册接口实现类
     *
     * @param umsAdminParam
     * @return
     */
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {

        UmsAdmin umsAdmin = new UmsAdmin();
        //copy传入对象的参数到数据库直连对象中,用的hutol的方法
        BeanUtil.copyProperties(umsAdminParam, umsAdmin);
        //并且为其添加创造时间与status属性(游客变用户)
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //验证(查询)数据库是否有相同用户名的用户
        //创造等值用户名条件
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        //如果返回的集合>0,说明已经有用户名和数据库中的用户名相同了
        List<UmsAdmin> umsAdminList = adminMapper.selectByExample(example);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //没有重名现象
        //对密码进行加密,这里需要注入security的加密工具类
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        adminMapper.insert(umsAdmin);
        return umsAdmin;
    }

    /**
     * 获取用户所有权限(包括角色权限和+-权限)
     * 根据用户Id查询权限列表和角色表等,多表查询
     *
     * @param adminId
     * @return
     */
    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {
        List<UmsPermission> permissionList = umsAdminRoleRelationDao.getPermissionList(adminId);
        return permissionList;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            //密码需要客户端加密后传递
            //1.通过用户名获取用户的全部信息与权限列表
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //2.通过matches方法进行比对密码,如果不匹配抛出异常
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
//        标准认证过程：
//        1.用户使用username和password登录
//        2.系统验证这个password对于该username是正确的
//        3.假设第二步验证成功，获取该用户的上下文信息（如他的角色列表）
//        4.围绕该用户建立安全上下文（security context）
//        5.用户可能继续进行的一些操作被一个验证控制机制潜在的管理，这个验证机制会根据当前用户的安全上下文来验证权限。

//        认证过程就是又前三项构成的。在Spring Security中是这样处理这三部分的：
//        1.username和password被获得后封装到一个UsernamePasswordAuthenticationToken（Authentication接口的实例）的实例中
//        2.这个token被传递给AuthenticationManager进行验证
//        3.成功认证后AuthenticationManager将返回一个得到完整填充的Authentication实例
//        4.通过调用SecurityContextHolder.getContext().setAuthentication(...)，参数传递authentication对象，来建立安全上下文（security context）
            //UsernamePasswordAuthenticationToken（Authentication接口的实例）的实例中,这个token被传递给AuthenticationManager进行验证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //建立安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //注入jwtTokenUtils,通过Springsecurity的UserDetails信息获得Token
            token = jwtTokenUtil.generateToken(userDetails);
//        //添加登录记录,这个接口还没写,暂时忽略
//        insertLoginLog(username);
        } catch (AuthenticationException e) {
            //本来是两个异常,这里合并成抓父类异常
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    /**
     * 刷新Token的功能
     *
     * @param oldToken
     * @return
     */
    @Override
    public String refreshToken(String oldToken) {
        //substring() 方法用于提取字符串中介于两个指定下标之间的子字符串,这里是从tokenHead的长度开始
        //获得之后的字符串内容,也就是Token的内容
        String token = oldToken.substring(tokenHead.length());
        if (jwtTokenUtil.canRefresh(token)) {
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }


    /**
     * 根据用户名或昵称分页查询用户
     *
     * @param name
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public List<UmsAdmin> list(String name, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsAdminExample example = new UmsAdminExample();
        UmsAdminExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            //根据用户名或者昵称,模糊查询,这里是添加条件
            criteria.andUsernameLike("%" + name + "%");
            example.or(example.createCriteria().andNickNameLike("%" + name + "%"));
        }
        return adminMapper.selectByExample(example);
    }

    @Override
    public UmsAdmin getItem(Long id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改信息这里因为密码是Springsecurity特殊加密的,所以还需要进行特别的修改
     * 且修改信息这里是非全的修改信息
     *
     * @param id
     * @param admin
     * @return
     */
    @Override
    public int update(Long id, UmsAdmin admin) {
        admin.setId(id);
        //密码已经被加密处理,需要单独修改
        admin.setPassword(null);
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Override
    public int delete(Long id) {
        return adminMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据用户id和角色列表,更新用户的角色(单表更新用户角色更新(插入用户角色关系表的数据))
     *
     * @param adminId
     * @param roleIds
     * @return
     */
    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        //如果用户没传角色,这里取值为0,
        //如果用户增加了角色,这里取值为用户添加的角色数量
        int count = roleIds == null ? 0 : roleIds.size();
            //先删除原来的角色与用户关系表中的关系,根据id删除
            UmsAdminRoleRelationExample example = new UmsAdminRoleRelationExample();
            example.createCriteria().andAdminIdEqualTo(adminId);
            //引入用户角色关系映射表的Mapper
            adminRoleRelationMapper.deleteByExample(example);
            //重新建立关系(更新关系)
            //如果超级管理员传入了角色集合
            if (!CollectionUtils.isEmpty(roleIds)) {
                List<UmsAdminRoleRelation> list = new ArrayList<>();
                for (Long roleId : roleIds) {
                    //新建用户角色关系对象接收
                    UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                    //将角色遍历出来,和用户id拼接在一起,组成一个个用户角色对象,存入集合中
                    roleRelation.setAdminId(adminId);
                    roleRelation.setRoleId(roleId);
                    list.add(roleRelation);
                }
                //进行组合插入,将整个集合中的对象,插入到数据库中,提高效率
                //这里用到的是自定义Dao接口,因为是组合插入,Mybatis的自动生成没有提供
                adminRoleRelationDao.insertList(list);
            }
            //返回角色数量,这里返回count的原因是,加入了事务控制,只有全部成功了,才能执行到最后一步,
            //如果有一处失败了,就全部执行失败,无法执行到最后一步.
            return count;
        }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return adminRoleRelationDao.getRoleList(adminId);
    }

    @Override
    public int updatePermission(Long adminId, List<Long> permissionId) {
        //1.同样是多对多的关系,所以更新权限,需要先删后增(用户表与自定义权限关系表)
        UmsAdminPermissionRelationExample example = new UmsAdminPermissionRelationExample();
        example.createCriteria().andAdminIdEqualTo(adminId);
        adminPermissionRelationMapper.deleteByExample(example);
        //2.获取用户所有角色权限
        //
        List<UmsPermission> permissionList = adminRoleRelationDao.getPermissionList(adminId);
        return 0;
    }
}

