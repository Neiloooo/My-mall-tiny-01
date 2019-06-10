package com.macro.mall.tiny.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.mapper.UmsAdminMapper;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsAdminExample;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.service.UmsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //引入springsecurity的加密机制
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 根据用户名获取管理员(用户全部信息)
     * @param username
     * @return
     */
    @Override
    public UmsAdmin getAdminByUsername(String username) {
        //根据用户名查询用户全部信息
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameNotEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(example);
        //如果数据库中有这个用户,将用户返回
        if (adminList !=null&&adminList.size()>0){
            return adminList.get(0);
        }
        return null;
    }

    /**
     * 注册接口实现类
     * @param umsAdminParam
     * @return
     */
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {

        UmsAdmin umsAdmin = new UmsAdmin();
        //copy传入对象的参数到数据库直连对象中,用的hutol的方法
        BeanUtil.copyProperties(umsAdminParam,umsAdmin);
        //并且为其添加创造时间与status属性(游客变用户)
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //验证(查询)数据库是否有相同用户名的用户
        //创造等值用户名条件
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        //如果返回的集合>0,说明已经有用户名和数据库中的用户名相同了
        List<UmsAdmin> umsAdminList = adminMapper.selectByExample(example);
        if (umsAdminList.size()>0){
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
     * @param adminId
     * @return
     */
    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {

        return null;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        //1.通过用户名获取用户的全部信息与权限列表
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //2.通过matches方法进行比对密码,如果不匹配抛出异常
        if (!passwordEncoder.matches(password,userDetails.getPassword())){
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
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return null;
    }
}
