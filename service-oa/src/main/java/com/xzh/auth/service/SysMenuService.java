package com.xzh.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.model.system.SysMenu;
import com.xzh.vo.system.AssginMenuVo;
import com.xzh.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author xzh
 * @since 2023-03-17
 */
public interface SysMenuService extends IService<SysMenu> {

    //菜单列表接口
    List<SysMenu> findNodes();

    //删除菜单
    void removeMenuById(Long id);

    //查询角色的菜单
    List<SysMenu> findMenuByRoleId(Long roleId);

    //给角色分配菜单
    void doAssign(AssginMenuVo assginMenuVo);

    //获取路由结构
    List<RouterVo> findUserMenuListByUser(Long userId);
    //获取按钮集合
    List<String> findUserPermsByUserId(Long userId);
}
