package com.xzh.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzh.model.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author:Xzh_
 * @Date:2023/3/14
 */
@Mapper
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
