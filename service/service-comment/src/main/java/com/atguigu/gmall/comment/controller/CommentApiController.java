package com.atguigu.gmall.comment.controller;

import com.atguigu.gmall.comment.service.CommentInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.comment.CommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentApiController {

    @Autowired
    private CommentInfoService commentInfoService;

    @GetMapping("/getCommentList")
    public Result getCommentList() {
        List<CommentInfo> commentInfoList = commentInfoService.getCommentList();
        return Result.ok(commentInfoList);
    }

}
