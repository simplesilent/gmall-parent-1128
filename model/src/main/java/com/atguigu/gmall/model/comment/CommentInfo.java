package com.atguigu.gmall.model.comment;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "用户评论")
public class CommentInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "skuid")
    @TableField("sku_id")
    private Long skuId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty(value = "评论内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "用户头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "评论时间")
    @TableField("date")
    private Date date;

    @TableField(exist = false)
    List<ReplyInfo> replyInfoList;

}
