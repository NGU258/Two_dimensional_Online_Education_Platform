package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.platform.mapper.UserMapper;
import com.edu.platform.model.User;
import com.edu.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 存储验证码的缓存，实际项目中应该使用Redis
    private static Map<String, CodeInfo> codeCache = new HashMap<>();
    
    // 验证码有效期：2分钟（120秒）
    private static final long CODE_EXPIRATION_TIME = 120 * 1000;
    
    // 验证码信息类
    private static class CodeInfo {
        private String code;
        private long timestamp;
        
        public CodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
        
        public String getCode() {
            return code;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        // 检查验证码是否过期
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CODE_EXPIRATION_TIME;
        }
    }

    @Override
    public User register(User user) {
        // 密码使用Base64编码，指定UTF-8字符集
        try {
            user.setPassword(Base64.getEncoder().encodeToString(user.getPassword().getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setRole(0); // 默认普通用户
        user.setStatus(1); // 默认启用
        user.setExperience(0);
        user.setLevel(1);
        user.setPoints(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // 确保 email 字段不为 null
        if (user.getEmail() == null) {
            user.setEmail("");
        }
        // 确保 nickname 字段不为 null
        if (user.getNickname() == null) {
            user.setNickname("");
        }
        // 确保 avatar 字段不为 null
        if (user.getAvatar() == null) {
            user.setAvatar("");
        }
        // 确保 phone 字段不为 null
        if (user.getPhone() == null) {
            user.setPhone("");
        }
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        // 尝试通过用户名查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        
        // 如果用户名查询不到，尝试通过邮箱查询
        if (user == null) {
            wrapper = new QueryWrapper<>();
            wrapper.eq("email", username);
            user = userMapper.selectOne(wrapper);
        }
        
        // 如果邮箱查询不到，尝试通过手机号查询
        if (user == null) {
            // 去除手机号中的+86前缀
            String phone = username.replace("+86", "");
            wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            user = userMapper.selectOne(wrapper);
        }
        
        // 验证密码
        try {
            if (user != null && user.getPassword().equals(Base64.getEncoder().encodeToString(password.getBytes("UTF-8")))) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User update(User user) {
        // 获取当前用户信息
        User currentUser = userMapper.selectById(user.getId());
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查手机号唯一性（如果手机号有变更）
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !user.getPhone().equals(currentUser.getPhone())) {
            // 去除手机号中的+86前缀
            String phone = user.getPhone().replace("+86", "").trim();
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", phone);
            phoneWrapper.ne("id", user.getId());
            if (userMapper.selectOne(phoneWrapper) != null) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
        }
        
        // 检查邮箱唯一性（如果邮箱有变更）
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().equals(currentUser.getEmail())) {
            String normalizedEmail = user.getEmail().trim().toLowerCase();
            QueryWrapper<User> emailWrapper = new QueryWrapper<>();
            emailWrapper.apply("lower(email) = {0}", normalizedEmail);
            emailWrapper.ne("id", user.getId());
            if (userMapper.selectOne(emailWrapper) != null) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }
        
        user.setUpdateTime(LocalDateTime.now());
        // 如果密码不为空，则进行Base64编码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            try {
                user.setPassword(Base64.getEncoder().encodeToString(user.getPassword().getBytes("UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User selectByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectByEmail(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // Case-insensitive 查询：lower(email) = ?
        wrapper.apply("lower(email) = {0}", normalizedEmail);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public String sendCode(String phone) {
        // 去除手机号中的+86前缀
        phone = phone.replace("+86", "");
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        // 存储验证码到缓存，包含时间戳
        codeCache.put(phone, new CodeInfo(code, System.currentTimeMillis()));
        // 实际项目中应该调用短信API发送验证码
        System.out.println("验证码已发送到手机号: " + phone + "，验证码: " + code + "，有效期: 2分钟");
        return code;
    }

    @Override
    public String sendEmailCode(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        // 存储验证码到缓存
        codeCache.put(normalizedEmail, new CodeInfo(code, System.currentTimeMillis()));
        // 实际项目中应该调用邮箱服务发送验证码
        System.out.println("邮箱验证码已发送到: " + normalizedEmail + "，验证码: " + code + "，有效期: 2分钟");
        return code;
    }

    @Override
    public User registerByPhone(String phone, String code, String password, String username, String email, String role) {
        phone = phone.replace("+86", "");
        CodeInfo codeInfo = codeCache.get(phone);
        if (codeInfo == null || codeInfo.isExpired() || !code.equals(codeInfo.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        if (selectByPhone(phone) != null) {
            throw new RuntimeException("手机号已注册");
        }
        if (username == null || username.isEmpty()) {
            username = "user_" + phone.substring(phone.length() - 4);
        }
        if (email == null || email.isEmpty()) {
            email = phone + "@example.com";
        }
        if (selectByUsername(username) != null) {
            username = "user_" + phone.substring(phone.length() - 4) + "_" + System.currentTimeMillis();
        }
        if (selectByEmail(email) != null) {
            email = phone + "_" + System.currentTimeMillis() + "@example.com";
        }
        User user = new User();
        user.setUsername(username);
        try {
            user.setPassword(Base64.getEncoder().encodeToString(password.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(convertRole(role));
        user.setStatus(1);
        user.setExperience(0);
        user.setLevel(1);
        user.setPoints(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setNickname("");
        user.setAvatar("");
        userMapper.insert(user);
        codeCache.remove(phone);
        return user;
    }
    
    private Integer convertRole(String role) {
        if (role == null) {
            return 0;
        }
        switch (role.toLowerCase()) {
            case "teacher":
                return 1;
            case "admin":
                return 2;
            case "student":
            default:
                return 0;
        }
    }

    @Override
    public User loginByPhone(String phone, String code) {
        // 去除手机号中的+86前缀
        phone = phone.replace("+86", "");
        // 验证验证码
        CodeInfo codeInfo = codeCache.get(phone);
        if (codeInfo == null || codeInfo.isExpired() || !code.equals(codeInfo.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        // 查询用户
        User user = selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 清除验证码
        codeCache.remove(phone);
        return user;
    }

    @Override
    public User selectByPhone(String phone) {
        // 去除手机号中的+86前缀
        phone = phone.replace("+86", "");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean resetPasswordByPhone(String phone, String code, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }

        phone = phone == null ? "" : phone.replace("+86", "").trim();
        CodeInfo codeInfo = codeCache.get(phone);
        if (codeInfo == null || codeInfo.isExpired() || !code.equals(codeInfo.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(newPassword);
        update(user); // update() 内部会把密码做 Base64 编码并写回数据库
        codeCache.remove(phone);
        return true;
    }

    @Override
    public boolean resetPasswordByEmail(String email, String code, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }

        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        CodeInfo codeInfo = codeCache.get(normalizedEmail);
        if (codeInfo == null || codeInfo.isExpired() || !code.equals(codeInfo.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = selectByEmail(normalizedEmail);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(newPassword);
        update(user);
        codeCache.remove(normalizedEmail);
        return true;
    }

    // 测试方法：生成密码的Base64编码
    public String getPasswordHash(String password) {
        try {
            return Base64.getEncoder().encodeToString(password.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 测试方法：验证密码是否匹配
    public boolean verifyPassword(String inputPassword, String hashedPassword) {
        try {
            String inputHash = Base64.getEncoder().encodeToString(inputPassword.getBytes("UTF-8"));
            return inputHash.equals(hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 测试方法：解码Base64编码的密码
    public String decodePassword(String encodedPassword) {
        try {
            return new String(Base64.getDecoder().decode(encodedPassword), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}