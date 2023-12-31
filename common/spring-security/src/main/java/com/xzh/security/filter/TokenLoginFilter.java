package com.xzh.security.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzh.common.jwt.JwtHelper;
import com.xzh.common.result.ResponseUtil;
import com.xzh.common.result.Result;
import com.xzh.common.result.ResultCodeEnum;
import com.xzh.security.custom.CustomUser;
import com.xzh.vo.system.LoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:Xzh_
 * @Date:2023/3/22
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
    private RedisTemplate redisTemplate;
    //构造方法
    public TokenLoginFilter(AuthenticationManager authenticationManager,
                            RedisTemplate redisTemplate){
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
        this.redisTemplate = redisTemplate;
    }
    //登录认证
    //获取输入的用户名和密码，调用方法认证
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            //获取用户信息
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            //封装对象
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            //调用框架的方法
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //认证成功调用的方法
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth)
            throws IOException, ServletException {
        //获取当前用户
        CustomUser customUser = (CustomUser) auth.getPrincipal(
        );
        //生成token
        String token = JwtHelper.createToken(customUser.getSysUser().getId(),
                                            customUser.getSysUser().getUsername());
        //获取当前用户权限数据 放到Redis里面 key：username value：权限数据
        redisTemplate.opsForValue().set(customUser.getUsername(),
                JSON.toJSONString(customUser.getAuthorities()));
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ResponseUtil.out(response, Result.ok(map));
    }
    //认证失败调用的方法
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException e)
            throws IOException, ServletException {
        if(e.getCause() instanceof RuntimeException) {
            ResponseUtil.out(response, Result.build(null, 204, e.getMessage()));
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.DATA_ERROR));
        }
    }
}
