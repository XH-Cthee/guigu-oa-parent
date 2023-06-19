package com.xzh.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.model.system.SysRole;
import com.xzh.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * @Author:Xzh_
 * @Date:2023/3/14
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByAdminId(Long userId);

    /**
     * 分配角色
     * @param assginRoleVo
     */
    void doAssign(AssginRoleVo assginRoleVo);
}
