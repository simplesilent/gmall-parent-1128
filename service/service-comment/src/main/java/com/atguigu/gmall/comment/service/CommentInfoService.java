package com.atguigu.gmall.comment.service;

import com.atguigu.gmall.model.comment.CommentInfo;

import java.util.List;

public interface CommentInfoService {

    /**查询评论列表*/
    List<CommentInfo> getCommentList();
}
