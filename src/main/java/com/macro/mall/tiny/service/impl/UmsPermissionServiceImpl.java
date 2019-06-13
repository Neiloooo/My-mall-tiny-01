package com.macro.mall.tiny.service.impl;

import com.macro.mall.tiny.mbg.mapper.UmsPermissionMapper;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.mbg.model.UmsPermissionExample;
import com.macro.mall.tiny.service.UmsPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UmsPermissionServiceImpl implements UmsPermissionService {

    @Autowired
    private UmsPermissionMapper permissionMapper;

    /**
     * 将权限封装进对象,再直接调用自动生成的单表Mapper
     * @param permission
     * @return
     */
    @Override
    public int create(UmsPermission permission) {
       permission.setStatus(1);
       permission.setCreateTime(new Date());
       permission.setSort(0);
       return permissionMapper.insert(permission);
    }

    @Override
    public int update(Long id, UmsPermission permission) {
        permission.setId(id);
        return permissionMapper.updateByPrimaryKey(permission);
    }

    /**
     * 批量删除,传入id的集合,将这些id全部删除
     * @param ids
     * @return
     */
    @Override
    public int delet(List<Long> ids) {
        UmsPermissionExample example = new UmsPermissionExample();
        //这里是自动生成的批量删除条件,andIdIn
        example.createCriteria().andIdIn(ids);
        return permissionMapper.deleteByExample(example);
    }

    @Override
    public List<UmsPermission>getlist() {
        //这里是根据非id查询,然后查询所有,就是无条件,就是new一个空条件对象
        return permissionMapper.selectByExample(new UmsPermissionExample());
    }


}
