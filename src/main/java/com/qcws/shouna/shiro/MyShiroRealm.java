package com.qcws.shouna.shiro;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.model.SysRole;
import com.qcws.shouna.model.SysUser;

import cn.hutool.core.util.StrUtil;

/**
 * 权限校验
 * 
 * @author: 恶魔恋
 * @date: 2020年9月21日 下午5:29:00
 * @version: v1.0.0
 */
public class MyShiroRealm extends AuthorizingRealm {

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}
	
	/**
	 * 校验当前用户的权限是否满足
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		UserDetail userDetail = (UserDetail) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorization = new SimpleAuthorizationInfo();
		if (userDetail.getRoles() != null) {
			authorization.addRoles(userDetail.getRoles());
		}
		if (userDetail.getPerms() != null) {
			authorization.addStringPermissions(userDetail.getPerms());
		}
		return authorization;
	}

	/**
	 * 验证登录信息，并封装返回登录后的用户信息
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println(token.toString());
		UsernamePasswordToken loginToken = (UsernamePasswordToken) token;
		SysUser user = SysUser.dao.findFirstByColumn("username", loginToken.getUsername());
		if (user == null) {
			throw new UnknownAccountException("没有找到该用户");
		}
		if (!user.getPassword().equals(String.valueOf(loginToken.getPassword()))) {
			throw new DisabledAccountException("账号或密码错误，请重新登录");
		}
		user.setLogintime(new Date());
		user.setLoginip(loginToken.getHost());
		user.update();
		
		// 如果是超级管理员，就更新他拥有所有的权限
		if ("root".equals(user.getRoles())) {
			Db.update("update sys_role set perms = (select group_concat(code) from sys_resource) where code = 'root'");
		}
		
		UserDetail userDetail = new UserDetail();
		userDetail.setId(user.getId());
		userDetail.setRealname(user.getRealname());
		userDetail.setUsername(user.getUsername());
		userDetail.setPassword(user.getPassword());
		if (StrUtil.isNotEmpty(user.getRoles())) {
			List<String> roles = Arrays.asList(user.getRoles().split(","));
			userDetail.setRoles(roles);
			if (roles != null) {
				roles.forEach(code -> {
					SysRole role = SysRole.dao.findFirstByColumn("code", code);
					if (role != null) {
						userDetail.addPerms(role.getPerms());
					}
				});
			}
		}
		userDetail.setLoginTime(user.getLogintime());
		userDetail.setLoginIp(loginToken.getHost());
		return new SimpleAuthenticationInfo(userDetail, user.getPassword(), userDetail.getRealname());
	}

}
