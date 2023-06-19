package com.xzh.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.model.system.SysUser;
import com.xzh.vo.system.RouterVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xzh
 * @since 2023-03-16
 */
public interface SysUserService extends IService<SysUser> {
    void updateStatus(Long id, Integer status);


    SysUser getUserByUserName(String username);

    Map<String, Object> getCurrentUser();

}
