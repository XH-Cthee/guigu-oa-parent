package com.xzh.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzh.auth.service.SysRoleService;
import com.xzh.common.config.exception.DIYException;
import com.xzh.common.result.Result;
import com.xzh.model.system.SysRole;
import com.xzh.vo.system.AssginRoleVo;
import com.xzh.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author:Xzh_
 * @Date:2023/3/15
 */
@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    /*@GetMapping("/findAll")
    public List<SysRole> findAll(){
        List<SysRole> list = sysRoleService.list();
        return list;
    }*/
    //统一返回结果
    @ApiOperation(value = "查询所有角色")
    @GetMapping("/findAll")
    public Result findAll(){
//        try{
//            int a=10/0;
//        }catch (Exception e){
//            //抛出自定义异常
//            throw new DIYException(20001,"执行了自定义异常..");
//        }

        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }
    //条件分页查询
    //page 当前页 limit 每页记录数 sysRoleQueryVo 条件对象
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        //1 创建page对象 获取当前页和每页记录数的参数
        Page<SysRole> pageParam = new Page<>(page,limit);
        //2 封装条件，判断是否为空，不为空再进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            //封装
            wrapper.like(SysRole::getRoleName,roleName);
        }
        //3 调用方法实现
        IPage<SysRole> pagemodel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pagemodel);

    }

    //添加
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation(value = "添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){
        boolean is_success = sysRoleService.save(role);
        if(is_success){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //修改
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "根据ID查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        boolean is_success = sysRoleService.updateById(role);
        if(is_success){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //根据ID删除
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("remove/{id}")
    public Result deleteById(@PathVariable Long id) {
        boolean is_success = sysRoleService.removeById(id);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
    //批量删除
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result deleteByBatches(@RequestBody List<Long> idList){
        boolean is_success = sysRoleService.removeByIds(idList);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
}
