package com.quickstart.common.domain.draw.vo;

import lombok.Data;

@Data
public class DrawSmallVO {

    private Long drawId;
    private String title;
    private String drawCover;

    // 发布者
    private String publisherMCode;
    // 发布者头像
    private String publisherAvatar;
}
