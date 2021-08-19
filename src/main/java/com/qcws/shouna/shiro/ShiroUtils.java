package com.qcws.shouna.shiro;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;

import com.qcws.shouna.model.SysResource;
import com.qcws.shouna.model.SysResourceGroup;

import cn.hutool.core.collection.CollectionUtil;

/**
 * shiro用户工具类
 * 
 * @author: 恶魔恋
 * @date: 2020年9月21日 下午5:30:04
 * @version: v1.0.0
 */
public class ShiroUtils {

	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static UserDetail getUserDetail() {
		return (UserDetail) SecurityUtils.getSubject().getPrincipal();
	}
	
	/**
	 * 获取当前登录用的id
	 * @return
	 */
	public static Integer getId() {
		return getUserDetail().getId();
	}
	
	/**
	 * 获取当前登录用户的用户名
	 * @return
	 */
	public static String getUsername() {
		return getUserDetail().getUsername();
	}
	
	/**
	 * 实际名称
	 * @return
	 */
	public static String getRealname() {
		return getUserDetail().getRealname();
	}

	/**
	 * 菜单列表
	 * @return
	 */
	public static List<MenuGroup> menuList() {
		List<String> resourceCodeList = getUserDetail().getPerms();
		List<MenuGroup> result = new ArrayList<MenuGroup>();
		List<SysResourceGroup> groupList = SysResourceGroup.dao.find("select * from sys_resource_group order by sort asc");
		List<SysResource> resourceList = SysResource.dao.find("select * from sys_resource order by sort asc");
		groupList.forEach(group -> {
			MenuGroup mg = new MenuGroup();
			try {
				BeanUtils.copyProperties(mg, group);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			for (SysResource resource : resourceList) {
				if (mg.getId().equals(resource.getGroupId())
						&& resourceCodeList.contains(resource.getCode())) {
					MenuItem item = new MenuItem();
					try {
						BeanUtils.copyProperties(item, resource);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					mg.addItem(item);
				}
			}
			if (CollectionUtil.isNotEmpty(mg.getItems())) {
				result.add(mg);
			}
		});
		return result;
	}

}

