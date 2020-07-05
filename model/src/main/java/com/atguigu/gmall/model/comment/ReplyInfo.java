package com.atguigu.gmall.model.comment;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "用户评论")
public class ReplyInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主评论id")
    @TableField("pid")
    private Long pid;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty(value = "评论内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "评论时间")
    @TableField("date")
    private Date date;

}
