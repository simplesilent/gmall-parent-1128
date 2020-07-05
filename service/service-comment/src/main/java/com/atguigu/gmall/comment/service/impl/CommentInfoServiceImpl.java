package com.atguigu.gmall.comment.service.impl;

import com.atguigu.gmall.comment.mapper.CommentInfoMapper;
import com.atguigu.gmall.comment.service.CommentInfoService;
import com.atguigu.gmall.model.comment.CommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentInfoServiceImpl implements CommentInfoService {

    @Autowired
    private CommentInfoMapper commentInfoMapper;

    @Override
    public List<CommentInfo> getCommentList() {
        return commentInfoMapper.getCommentList();
    }
}
