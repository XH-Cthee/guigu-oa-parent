package com.xzh.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xzh.auth.mapper.SysRoleMapper;
import com.xzh.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @Author:Xzh_
 * @Date:2023/3/14
 */
@SpringBootTest
public class TestMpDemo01 {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    //查询所有
    @Test
    public void getAll(){
        List<SysRole> list = sysRoleMapper.selectList(null);
        System.out.println(list);
    }

    //添加操作
    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("停车场管理员");
        sysRole.setRoleCode("role01");
        sysRole.setDescription("停车场管理员");

        int rows = sysRoleMapper.insert(sysRole);//增加的行数
        System.out.println(rows);
        System.out.println(sysRole);
    }
    //修改操作
    @Test
    public void updateById(){
        //根据ID查询
        SysRole sysRole = sysRoleMapper.selectById(9);
        //设置修改值
        sysRole.setRoleName("龙卷风摧毁停车场");
        //调用方法最终实现
        int rows = sysRoleMapper.updateById(sysRole);
        System.out.println(rows);
    }
    //逻辑删除操作
    @Test
    public void deleteById(){
        int rows = sysRoleMapper.deleteById(9);
    }
    //批量逻辑删除操作
    @Test
    public void deleteByBatches(){
        int rows = sysRoleMapper.deleteBatchIds(Arrays.asList(1,2));
        System.out.println(rows);
    }
    //条件查询
    @Test
    public void testQuery(){
        //创建QueryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","系统管理员");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println(list);
    }
    //条件查询-Lambda
    @Test
    public void testQueryByLambda(){
        //创建QueryWrapper对象，调用方法封装条件
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRole::getRoleName,"普通管理员");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println(list);
    }
}
