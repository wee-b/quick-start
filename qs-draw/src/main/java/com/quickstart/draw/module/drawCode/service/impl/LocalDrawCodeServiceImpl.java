package com.quickstart.draw.module.drawCode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickstart.common.domain.ErrorCode;
import com.quickstart.common.domain.draw.Draw;
import com.quickstart.draw.constant.DrawConstants;
import com.quickstart.common.domain.drawCode.DrawCode;
import com.quickstart.common.domain.drawCode.vo.DrawCodeVO;
import com.quickstart.common.exception.BusinessException;
import com.quickstart.draw.module.draw.mapper.DrawMapper;
import com.quickstart.draw.module.drawCode.mapper.DrawCodeMapper;
import com.quickstart.draw.module.drawCode.service.DrawCodeService;
import com.quickstart.draw.util.DrawCodeGenerator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "qs.rabbitmq.enabled", havingValue = "false")
public class LocalDrawCodeServiceImpl implements DrawCodeService {


    // 注入 线程安全单例 唯一码生成器
    @Resource
    private DrawCodeGenerator drawCodeGenerator;

    @Autowired
    private DrawCodeMapper drawCodeMapper;
    @Autowired
    private DrawMapper drawMapper;


    /**
     * 参与抽奖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> joinDraw(Long drawId, Long userId) {
        // 1. 查询抽签活动并校验是否可参与
        Draw draw = drawMapper.selectById(drawId);
        ensureJoinable(draw);

        // 2. 获取每人可生成的码数量
        int perCodeNum = draw.getPerCodeNum();
        if (perCodeNum <= 0) {
            throw new IllegalArgumentException("每人参与码数量配置错误");
        }

        // ========== 核心：调用线程安全的单例生成器 ==========
        List<String> codeValues = drawCodeGenerator.batchGenerate(perCodeNum);

        // 3. 组装批量插入数据
        List<DrawCode> drawCodeList = new ArrayList<>(codeValues.size());
        LocalDateTime now = LocalDateTime.now();
        for (String codeValue : codeValues) {
            DrawCode code = new DrawCode();
            code.setDrawId(drawId);
            code.setUserId(userId);
            code.setCodeValue(codeValue);
            code.setCreateTime(now);
            drawCodeList.add(code);
        }

        // 4. 批量插入
        drawCodeMapper.batchInsert(drawCodeList);

        return codeValues;
    }

    private void ensureJoinable(Draw draw) {
        if(draw == null){
            throw new IllegalArgumentException("抽奖不存在");
        }
        // 校验抽奖状态：必须是进行中
        if (draw.getStatus() == null || draw.getStatus() != DrawConstants.DRAW_STATUS_RUNNING) {
            throw new IllegalArgumentException("当前抽奖已结束，无法参与");
        }
        // 校验参与截止时间：未超时才能参与
        if (draw.getJoinDeadline() != null && draw.getJoinDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("抽奖已截止，无法参与");
        }
    }

    /**
     * 查询我的抽奖码
     */
    @Override
    public List<DrawCodeVO> getMyCodes(Long drawId, Long userId) {
        Draw draw = drawMapper.selectById(drawId);

        LambdaQueryWrapper<DrawCode> dcLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dcLambdaQueryWrapper.eq(DrawCode::getDrawId, draw.getDrawId());
        dcLambdaQueryWrapper.eq(DrawCode::getUserId, userId);

        List<DrawCode> codes = drawCodeMapper.selectList(dcLambdaQueryWrapper);
        if (codes.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "您还没有参加过该抽签");
        }

        LocalDateTime now = LocalDateTime.now();
        Boolean isOpen = draw.getDrawTime().isBefore(now);
        return codes.stream().map(one -> {
            DrawCodeVO vo = new DrawCodeVO();
            vo.setCodeValue(one.getCodeValue());
            String desc;
            if (one.getPrizeId() != null) {
                vo.setPrizeId(one.getPrizeId());
                desc = "已中奖";
            } else if (isOpen) {
                desc = "未中奖";
            } else {
                desc = "未开奖";
            }
            vo.setDesc(desc);
            return vo;
        }).collect(Collectors.toList());
    }


    @Override
    public void openDraw(Long drawId, Long userId) {

    }




}