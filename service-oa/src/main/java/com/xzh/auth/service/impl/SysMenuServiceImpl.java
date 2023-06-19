package com.xzh.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzh.auth.mapper.SysMenuMapper;
import com.xzh.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzh.auth.service.SysRoleMenuService;
import com.xzh.auth.utils.MenuHelper;
import com.xzh.common.config.exception.DIYException;
import com.xzh.model.system.SysMenu;
import com.xzh.model.system.SysRoleMenu;
import com.xzh.vo.system.AssginMenuVo;
import com.xzh.vo.system.MetaVo;
import com.xzh.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author xzh
 * @since 2023-03-17
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        //查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //构建树形结构
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
     //判断是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0){
            throw new DIYException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    //查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 1 查询所有菜单 根据status=1 条件
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        // 2 根据橘色roleId在关系表里面查询所属的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        // 3 根据获取的菜单id 获取对应的菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
        // 3.1 用菜单id和所有菜单集合里面的id比较 相同就封装
        allSysMenuList.stream().forEach(item ->{
            if(menuIdList.contains(item.getId())){
                item.setSelect(true);
            }else{
                item.setSelect(false);
            }
        });
        // 4 返回树形格式 显示的菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    //给角色分配菜单
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        // 1 根据角色ID删除之前分配过的菜单
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);
        // 2 从参数里面获取角色新分配的ID列表 进行遍历 把id数据添加进角色表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for(Long menuId:menuIdList){
            if(StringUtils.isEmpty(menuId)){
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    //根据用户ID获取用户可以操作的菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUser(Long userId) {
        //1 判断当前用户是否是管理员 userId=1 是管理员
        List<SysMenu> sysMenuList = null;
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        }else {
            //2 不是管理员就查询可以操作的按钮列表 多表联查
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
            //使用菜单操作工具构建树形结构
            List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
            //构建成框架要求的路由结构
            //3 从查出来的数据获取可以操作的按钮值的list集合并返回
            List<RouterVo> routerList = this.buildRouter(sysMenuTreeList);
            return routerList;
    }

    private List<RouterVo> buildRouter(List<SysMenu> menus) {
            //创建list集合存储最终数据
        ArrayList<RouterVo> routers = new ArrayList<>();
        //menus遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if(menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }//递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;

    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        if(userId.longValue()==1){
            //查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        }else {
            //不是管理员就跟根据userId查询可以操作的按钮列表
            //多表联查
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //3 从查出来的数据里面获取可以操作的按钮值的list集合并返回
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());
        return permsList;
    }
}
