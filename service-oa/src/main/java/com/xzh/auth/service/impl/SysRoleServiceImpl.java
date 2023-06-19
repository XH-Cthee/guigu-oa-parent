package com.xzh.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xzh.auth.mapper.SysRoleMapper;
import com.xzh.auth.service.SysRoleService;
import com.xzh.auth.service.SysUserRoleService;
import com.xzh.model.system.SysRole;
import com.xzh.model.system.SysUserRole;
import com.xzh.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:Xzh_
 * @Date:2023/3/14
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    //查询所有角色和 当前用户所属角色
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        //1 查询所有角色 返回list集合
        List<SysRole> allRoleList = baseMapper.selectList(null);
        //2 根据 userId查询 角色用户关系表，查询userId对应的所有角色Id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        //一个用户所对应的角色的集合
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);
        //所属角色的ID集合
//        ArrayList<Long> list = new ArrayList<>();
//        for (SysUserRole sysUserRole:existUserRoleList){
//            Long roleId = sysUserRole.getRoleId();
//            list.add(roleId);
//        }和下面是等价写法 下面是Java8新特性的stream流和lambda表达式的写法
        List<Long> existRoleIdList = existUserRoleList.stream().map(c->c.getRoleId()).collect(Collectors.toList());
        //3 根据查询到的角色ID集合到所有角色集合中比较ID是否存在-》 有就添加反之则然
        List<SysRole> assignRoleList = new ArrayList<>();
        for(SysRole sysRole : allRoleList){
            //比较是否存在
            if(existRoleIdList.contains(sysRole.getId())){
                assignRoleList.add(sysRole);
            }
        }
        //4 把得到的两部分数据 封装进map集合
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
        roleMap.put("allRolesList", allRoleList);
        return roleMap;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //把用户之前分配的角色数据删除完，用户角色关系表里面，根据userId删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        //重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for(Long roleId:roleIdList){
            if(StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }

    }
}
