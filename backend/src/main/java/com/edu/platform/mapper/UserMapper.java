package com.edu.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.platform.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}