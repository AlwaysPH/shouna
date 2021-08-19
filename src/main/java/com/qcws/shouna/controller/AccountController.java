package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.SysUser;
import com.qcws.shouna.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.jfinal.kit.Ret;
import com.qcws.shouna.config.R;
import com.qcws.shouna.shiro.UserDetail;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin/account")
public class AccountController extends JbootController {

	@Inject
	private SysUserService sysUserService;

	public void captcha() { renderCaptcha(); }
	
	public void login() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated() && subject.getPrincipal() != null) {
			redirect("/admin/index");
			return;
		}
		
		// 判断是否记住了登陆
		if (subject.isRemembered() && subject.getPrincipal() != null) {
			UserDetail rememberUser = (UserDetail) subject.getPrincipal();
			UsernamePasswordToken token = new UsernamePasswordToken(rememberUser.getUsername(), rememberUser.getPassword(), true, getIPAddress());
			try {
				subject.login(token);
				redirect("/admin/index");
			} catch (AuthenticationException e) {
				log.error("记住登陆状态后重新登陆失败：" + e.getMessage());
			}
		}
		
		if (getRequest().getMethod().equalsIgnoreCase("post")) {
			if (!validateCaptcha("vcode")) {
				renderJson(Ret.fail(R.MSG, "验证码输入错误"));
				return;
			}
			String username = getPara("username");
			String password = getPara("password");
			UsernamePasswordToken token = new UsernamePasswordToken();
			token.setUsername(username);
			token.setPassword(password.toCharArray());
			token.setHost(getIPAddress());
			token.setRememberMe(true);
			try {
				subject.login(token);
				renderJson(Ret.ok());
			} catch (Exception e) {
				renderJson(Ret.fail(R.MSG, e.getMessage()));
			}
		}
	}
	
	public void logout() {
		SecurityUtils.getSubject().logout();
		redirect("/admin/account/login");
	}

	public void updatePassword(){}

	public void save(){
		String oldPassword = getPara("oldPassword");
		String newPassword = getPara("newPassword");
		if(oldPassword.equals(newPassword)){
			renderHtml(new MessageBox(false).toString());
			return;
		}
		UserDetail userDetail = (UserDetail) SecurityUtils.getSubject().getPrincipal();
		if(!oldPassword.equals(userDetail.getPassword())){
			renderHtml(new MessageBox(false).toString());
			return;
		}
		SysUser user = new SysUser();
		user.setPassword(newPassword);
		user.setId(userDetail.getId());
		user.saveOrUpdate();
		renderHtml(new MessageBox(true).toString());
	}
}
