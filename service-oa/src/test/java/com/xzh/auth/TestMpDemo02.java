package com.xzh.auth;

import com.xzh.auth.service.SysRoleService;
import com.xzh.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author:Xzh_
 * @Date:2023/3/14
 */
@SpringBootTest
public class TestMpDemo02 {
    @Autowired
    private SysRoleService sysRoleService;

    //查询所有
    @Test
    public void testGetAll(){
        List<SysRole> list = sysRoleService.list();
        System.out.println(list);
    }


}
