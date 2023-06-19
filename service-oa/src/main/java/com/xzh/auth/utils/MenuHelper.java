package com.xzh.auth.utils;

import com.xzh.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Xzh_
 * @Date:2023/3/17
 */
public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        //创建list集合 用于最终数据
        ArrayList<SysMenu> trees = new ArrayList<>();
        //遍历所有菜单数据
        for(SysMenu sysMenu:sysMenuList){
            //parentId=0 是递归入口
            if(sysMenu.getParentId().longValue()==0){
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }
    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList){
        sysMenu.setChildren(new ArrayList<SysMenu>());

        for (SysMenu it : sysMenuList) {
            if(sysMenu.getId().longValue() == it.getParentId().longValue()) {
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return sysMenu;
    }

}
