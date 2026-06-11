package com.edu.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.platform.mapper.UserMapper;
import com.edu.platform.model.User;
import com.edu.platform.service.UserService;
import com.edu.platform.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理", description = "用户注册、登录、信息管理等操作")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "注册新用户，需要提供用户名、密码和邮箱")
    public Map<String, Object> register(@ApiParam(name = "user", value = "用户信息", required = true) @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查用户名是否已存在
            if (userService.selectByUsername(user.getUsername()) != null) {
                result.put("code", 400);
                result.put("message", "用户名已存在");
                return result;
            }
            // 检查邮箱是否已存在
            if (userService.selectByEmail(user.getEmail()) != null) {
                result.put("code", 400);
                result.put("message", "邮箱已存在");
                return result;
            }
            User registeredUser = userService.register(user);
            // 注册成功后生成token
            String token = JwtUtils.generateToken(registeredUser.getId(), registeredUser.getUsername());
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("token", token);
            result.put("user", registeredUser);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录，需要提供用户名和密码")
    public Map<String, Object> login(@ApiParam(name = "loginInfo", value = "登录信息，包含用户名和密码", required = true) @RequestBody Map<String, String> loginInfo) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            System.out.println("Login attempt with username: " + username);
            
            // 先检查用户是否存在
            User user = null;
            String loginType = "用户";
            
            // 检查是否是邮箱格式
            if (username.contains("@")) {
                loginType = "邮箱";
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("email", username);
                user = userMapper.selectOne(wrapper);
            } else if (username.matches("^1[3-9]\\d{9}$")) {
                // 如果是手机号格式
                loginType = "手机号";
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("phone", username);
                user = userMapper.selectOne(wrapper);
            } else {
                // 否则视为用户名
                loginType = "用户名";
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username", username);
                user = userMapper.selectOne(wrapper);
            }
            
            if (user == null) {
                result.put("code", 401);
                result.put("message", loginType + "未注册");
            } else {
                // 验证密码
                try {
                    if (user.getPassword().equals(Base64.getEncoder().encodeToString(password.getBytes("UTF-8")))) {
                        System.out.println("Generating token for user: " + user.getId() + " " + user.getUsername());
                        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
                        System.out.println("Token generated: " + token);
                        result.put("code", 200);
                        result.put("message", "登录成功");
                        result.put("token", token);
                        result.put("user", user);
                    } else {
                        result.put("code", 401);
                        result.put("message", "密码错误");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("code", 401);
                    result.put("message", "密码错误");
                }
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "登录失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取用户信息", notes = "根据token获取用户信息")
    public Map<String, Object> getUserInfo(@ApiParam(name = "Authorization", value = "Bearer token", required = true) @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = JwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            User user = userService.getById(userId);
            if (user != null) {
                System.out.println("User info role: " + user.getRole());
                result.put("code", 200);
                result.put("message", "获取用户信息成功");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取用户信息失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息，需要提供token和用户信息")
    public Map<String, Object> updateUser(@ApiParam(name = "Authorization", value = "Bearer token", required = true) @RequestHeader("Authorization") String token, @ApiParam(name = "user", value = "用户信息", required = true) @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = JwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            user.setId(userId);
            
            // 检查用户名是否已存在（排除当前用户）
            User existingUserByUsername = userService.selectByUsername(user.getUsername());
            if (existingUserByUsername != null && !existingUserByUsername.getId().equals(userId)) {
                result.put("code", 400);
                result.put("message", "用户名已存在");
                return result;
            }
            
            User updatedUser = userService.update(user);
            result.put("code", 200);
            result.put("message", "更新用户信息成功");
            result.put("data", updatedUser);
        } catch (RuntimeException e) {
            // 处理唯一性校验等业务逻辑错误
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            // 处理其他系统错误
            result.put("code", 500);
            result.put("message", "更新用户信息失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/send-code")
    @ApiOperation(value = "发送验证码", notes = "向手机号发送验证码")
    public Map<String, Object> sendCode(@ApiParam(name = "phone", value = "手机号", required = true) @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = request.get("phone");
            String code = userService.sendCode(phone);
            result.put("code", 200);
            result.put("message", "验证码已发送");
            result.put("data", code);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "发送验证码失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/send-email-code")
    @ApiOperation(value = "发送邮箱验证码", notes = "向邮箱发送验证码")
    public Map<String, Object> sendEmailCode(@ApiParam(name = "email", value = "邮箱", required = true) @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = request.get("email");
            String code = userService.sendEmailCode(email);
            result.put("code", 200);
            result.put("message", "邮箱验证码已发送");
            result.put("data", code);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "发送邮箱验证码失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/reset-password/phone")
    @ApiOperation(value = "重置密码（手机号）", notes = "使用手机号和验证码重置密码")
    public Map<String, Object> resetPasswordByPhone(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = request.get("phone");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            userService.resetPasswordByPhone(phone, code, newPassword);
            result.put("code", 200);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/reset-password/email")
    @ApiOperation(value = "重置密码（邮箱）", notes = "使用邮箱和验证码重置密码")
    public Map<String, Object> resetPasswordByEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = request.get("email");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            userService.resetPasswordByEmail(email, code, newPassword);
            result.put("code", 200);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/register/phone")
    @ApiOperation(value = "手机注册", notes = "使用手机号和验证码注册新用户")
    public Map<String, Object> registerByPhone(@ApiParam(name = "registerInfo", value = "注册信息，包含手机号、验证码、密码、用户名、邮箱和角色", required = true) @RequestBody Map<String, String> registerInfo) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = registerInfo.get("phone");
            String code = registerInfo.get("code");
            String password = registerInfo.get("password");
            String username = registerInfo.get("username");
            String email = registerInfo.get("email");
            String role = registerInfo.get("role");
            System.out.println("Register role from frontend: " + role);
            User user = userService.registerByPhone(phone, code, password, username, email, role);
            System.out.println("Registered user role: " + user.getRole());
            String token = JwtUtils.generateToken(user.getId(), user.getUsername());
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("token", token);
            result.put("user", user);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", "注册失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/login/phone")
    @ApiOperation(value = "手机登录", notes = "使用手机号和验证码登录")
    public Map<String, Object> loginByPhone(@ApiParam(name = "loginInfo", value = "登录信息，包含手机号和验证码", required = true) @RequestBody Map<String, String> loginInfo) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = loginInfo.get("phone");
            String code = loginInfo.get("code");
            User user = userService.loginByPhone(phone, code);
            String token = JwtUtils.generateToken(user.getId(), user.getUsername());
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", user);
        } catch (Exception e) {
            result.put("code", 401);
            result.put("message", "登录失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/test/password")
    @ApiOperation(value = "测试密码", notes = "生成密码的Base64编码、验证密码是否匹配或解码Base64编码的密码")
    public Map<String, Object> testPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String password = request.get("password");
            String hashedPassword = request.get("hashedPassword");
            String encodedPassword = request.get("encodedPassword");
            
            if (password != null && hashedPassword == null && encodedPassword == null) {
                // 生成密码的Base64编码
                String hash = ((com.edu.platform.service.impl.UserServiceImpl) userService).getPasswordHash(password);
                result.put("code", 200);
                result.put("message", "生成密码Base64编码成功");
                result.put("password", password);
                result.put("encodedPassword", hash);
            } else if (password != null && hashedPassword != null) {
                // 验证密码是否匹配
                boolean isMatch = ((com.edu.platform.service.impl.UserServiceImpl) userService).verifyPassword(password, hashedPassword);
                result.put("code", 200);
                result.put("message", "验证密码成功");
                result.put("password", password);
                result.put("encodedPassword", hashedPassword);
                result.put("isMatch", isMatch);
            } else if (encodedPassword != null) {
                // 解码Base64编码的密码
                String decodedPassword = ((com.edu.platform.service.impl.UserServiceImpl) userService).decodePassword(encodedPassword);
                result.put("code", 200);
                result.put("message", "解码密码成功");
                result.put("encodedPassword", encodedPassword);
                result.put("decodedPassword", decodedPassword);
            } else {
                result.put("code", 400);
                result.put("message", "参数错误：请提供password参数或encodedPassword参数");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "测试密码失败: " + e.getMessage());
        }
        return result;
    }
}