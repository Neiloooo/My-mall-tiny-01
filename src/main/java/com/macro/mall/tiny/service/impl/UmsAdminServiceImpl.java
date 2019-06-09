package com.macro.mall.tiny.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.mapper.UmsAdminMapper;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsAdminExample;
import com.macro.mall.tiny.service.UmsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
