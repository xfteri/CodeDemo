package cn.edu.uzz.service;

import cn.edu.uzz.pojo.Admin;


public interface AdminService {
    /**
     * 登录
     *
     * @param name 名字
     * @param pwd  密码
     * @return {@code Admin}
     */
    Admin login(String name, String pwd);
}
