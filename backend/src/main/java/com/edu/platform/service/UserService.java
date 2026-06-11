package com.edu.platform.service;

import com.edu.platform.model.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User getById(Long id);
    User update(User user);
    User selectByUsername(String username);
    User selectByEmail(String email);
    // 手机相关方法
    String sendCode(String phone);
    String sendEmailCode(String email);
    User registerByPhone(String phone, String code, String password, String username, String email, String role);
    User loginByPhone(String phone, String code);
    User selectByPhone(String phone);

    // 找回密码相关方法（验证码校验后重置密码）
    boolean resetPasswordByPhone(String phone, String code, String newPassword);
    boolean resetPasswordByEmail(String email, String code, String newPassword);
}