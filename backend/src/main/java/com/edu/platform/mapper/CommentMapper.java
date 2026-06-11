package com.edu.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.platform.model.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
