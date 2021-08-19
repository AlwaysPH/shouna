package com.qcws.shouna.shiro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 用于封装shiro登录信息的javabean <hr />
 * 
 * roles: [admin, user]<br />
 * perms: [user_add, user_list, user_update...]
 * @author: 恶魔恋
 * @date: 2020年9月21日 下午5:31:36
 * @version: v1.0.0
 */
@Data
public class UserDetail implements Serializable {

	private static final long serialVersionUID = 4005261428576764798L;
	
	private Integer id;
	private String realname;
	private String username;
	private String password;
	private Date loginTime;
	private String loginIp;
	private List<String> roles = new ArrayList<String>();
	private List<String> perms = new ArrayList<String>();
	
	private void addPerm(String perm) {
		if (perms == null) { perms = new ArrayList<String>(); }
		perms.add(perm);
	}
	
	public void addPerms(String perms) {
		if (perms == null) { return; }
		String[] permArray = perms.split(",");
		for (String perm : permArray) {
			addPerm(perm);
		}
	}
}
