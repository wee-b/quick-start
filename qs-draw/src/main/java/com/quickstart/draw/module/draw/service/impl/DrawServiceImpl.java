package com.quickstart.draw.module.draw.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickstart.common.domain.ErrorCode;
import com.quickstart.common.domain.draw.Draw;

import com.quickstart.common.domain.draw.dto.DrawCreateRequest;
import com.quickstart.common.domain.draw.dto.GenerateCodeDTO;
import com.quickstart.common.domain.draw.vo.*;
import com.quickstart.common.exception.BusinessException;

import com.quickstart.draw.constant.DrawConstants;
import com.quickstart.draw.constant.RedisConstant;
import com.quickstart.draw.module.draw.mapper.DrawMapper;
import com.quickstart.draw.module.draw.service.DrawService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class DrawServiceImpl implements DrawService {


    @Resource
    private DrawMapper drawMapper;
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 获取官方抽奖
     * @return
     */
    @Override
    public List<DrawSmallVO> getOfficialDraw() {

        LambdaQueryWrapper<Draw> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Draw::getPublisherUserId,0);
        queryWrapper.eq(Draw::getStatus, DrawConstants.DRAW_STATUS_RUNNING);
        queryWrapper.eq(Draw::getDeletedFlag,0);
        queryWrapper.orderByDesc(Draw::getCreateTime);

        List<Draw> draws = drawMapper.selectList(queryWrapper);
        List<DrawSmallVO> officialDraws = draws.stream().map(one->{
            DrawSmallVO vo = new DrawSmallVO();
            BeanUtils.copyProperties(one,vo);
            return vo;
        }).toList();
        return officialDraws;
    }

    /**
     * 创建抽签
     * @param request
     * @param publisherUserId
     * @return
     */
    @Override
    @Transactional
    public DrawVO createDraw(DrawCreateRequest request, Long publisherUserId) {
        validateDeadline(request.getJoinDeadline());

        Draw draw = new Draw();
        draw.setPublisherUserId(publisherUserId);
        draw.setTitle(request.getTitle());
        draw.setDrawCover(request.getDrawCover());
        draw.setDescription(request.getDescription());
        draw.setHasPrize(request.getHasPrize());
        draw.setDrawingWay(request.getDrawingWay());
        draw.setJoinDeadline(request.getJoinDeadline());
        draw.setMinPerson(request.getMinPerson() == null ? 0 : request.getMinPerson());
        draw.setPerCodeNum(request.getPerCodeNum() == null ? 5 : request.getPerCodeNum());

        // 系统自动生成
        String drawNo = String.valueOf(snowflake.nextId());
        draw.setDrawNo(drawNo);
        draw.setDrawTime(null);
        draw.setStatus(DrawConstants.DRAW_STATUS_DRAFT);
        draw.setParticipantCount(0);
        draw.setCodeCount(0);
        draw.setDeletedFlag(0);
        draw.setCreateTime(LocalDateTime.now());
        draw.setUpdateTime(LocalDateTime.now());

        drawMapper.insert(draw);

        // 组装返回VO（完全按你要求的字段）
        DrawVO vo = new DrawVO();
        vo.setDrawId(draw.getDrawId());
        vo.setTitle(draw.getTitle());
        vo.setDrawCover(draw.getDrawCover());
        vo.setDescription(draw.getDescription());
        vo.setHasPrize(draw.getHasPrize());
        vo.setDrawingWay(draw.getDrawingWay());
        vo.setJoinDeadline(draw.getJoinDeadline());
        vo.setMinPerson(draw.getMinPerson());
        vo.setPerCodeNum(draw.getPerCodeNum());
        vo.setDrawNo(draw.getDrawNo());
        vo.setCreateTime(draw.getCreateTime());

        return vo;
    }
    private void validateDeadline(LocalDateTime joinDeadline) {
        if (joinDeadline == null) {
            throw new IllegalArgumentException("参与截止时间不能为空");
        }
        if (joinDeadline.isAfter(LocalDateTime.now().plusDays(DrawConstants.MAX_DRAW_EXPIRE_DAYS))) {
            throw new IllegalArgumentException("参与截止时间不能超过最大有效期");
        }
    }


    @Override
    public DrawVO getDetailDraw(Long drawId) {
        Draw draw = drawMapper.selectById(drawId);
        DrawVO vo = new DrawVO();
        BeanUtils.copyProperties(draw,vo);
        return vo;
    }

    @Override
    @Transactional
    public DrawVO updateDraw(DrawCreateRequest request, Long userId) {
        Draw draw = drawMapper.selectById(request.getDrawId());
        if (draw == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "抽签不存在");
        }
        if (!draw.getPublisherUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己发布的抽签");
        }
        if (draw.getStatus() != DrawConstants.DRAW_STATUS_DRAFT) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "草稿状态的抽签才可修改");
        }

        validateDeadline(request.getJoinDeadline());

        draw.setTitle(request.getTitle());
        draw.setDrawCover(request.getDrawCover());
        draw.setDescription(request.getDescription());
        draw.setHasPrize(request.getHasPrize());
        draw.setDrawingWay(request.getDrawingWay());
        draw.setJoinDeadline(request.getJoinDeadline());
        draw.setMinPerson(request.getMinPerson() == null ? 0 : request.getMinPerson());
        draw.setPerCodeNum(request.getPerCodeNum() == null ? 5 : request.getPerCodeNum());
        draw.setUpdateTime(LocalDateTime.now());

        drawMapper.updateById(draw);

        DrawVO vo = new DrawVO();
        BeanUtils.copyProperties(draw, vo);
        return vo;
    }

    @Override
    @Transactional
    public void deleteDraw(Long drawId, Long userId) {
        Draw draw = drawMapper.selectById(drawId);
        if (draw == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "抽签不存在");
        }
        if (!draw.getPublisherUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己发布的抽签");
        }
        if (draw.getStatus() != DrawConstants.DRAW_STATUS_DRAFT) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "草稿状态的抽签才可删除");
        }

        draw.setDeletedFlag(1);
        draw.setUpdateTime(LocalDateTime.now());
        drawMapper.updateById(draw);
    }

    @Override
    @Transactional
    public void publishDraw(Long drawId, Long userId) {
        Draw draw = drawMapper.selectById(drawId);
        if (draw == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "抽签不存在");
        }
        if (!draw.getPublisherUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能发布自己创建的抽签");
        }
        if (draw.getStatus() != DrawConstants.DRAW_STATUS_DRAFT) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有草稿状态的抽签才能发布");
        }

        draw.setStatus(DrawConstants.DRAW_STATUS_RUNNING);
        draw.setUpdateTime(LocalDateTime.now());
        drawMapper.updateById(draw);
    }

    @Override
    public List<Draw> listExpiredRunningDraws() {
        return drawMapper.selectExpiredRunningDraws();
    }



    @Override
    public void generatePassCode(Long userId, GenerateCodeDTO dto) {

        Long drawId = dto.getDrawId();
        Draw draw = drawMapper.selectById(drawId);
        if(draw == null){
            throw new IllegalArgumentException("抽奖不存在");
        }
        if(!draw.getPublisherUserId().equals(userId)){
            throw new IllegalArgumentException("不可发布他人抽奖的口令");
        }

        String prefix = RedisConstant.PassCodePrefix;
        String passcode = "";
        boolean generateSuccess = false;
        for(int i = 0;i<3;i++){     // 尝试3次
            passcode = String.valueOf(generateSixDigitCode());
            String query = redisTemplate.opsForValue().get(prefix+passcode);

            if(query == null){
                generateSuccess = true;
                break;
            }
        }
        if(!generateSuccess){
            throw new BusinessException("请稍后再试");
        }

        Integer expireHours = dto.getExpireHours();
        redisTemplate.opsForValue().set(prefix+passcode, String.valueOf(drawId),expireHours,TimeUnit.HOURS);
        redisTemplate.opsForValue().set(prefix+drawId, passcode,expireHours,TimeUnit.HOURS);
    }

    @Override
    public void banPassCode(Long userId, String passCode) {
        String prefix = RedisConstant.PassCodePrefix;

        // 1. 根据口令查询对应的抽奖ID（你之前存的：prefix+passCode -> drawId）
        String passCodeKey = prefix + passCode;
        String drawIdStr = redisTemplate.opsForValue().get(passCodeKey);
        if (drawIdStr == null) {
            throw new BusinessException("口令不存在或已失效");
        }
        Long drawId = Long.parseLong(drawIdStr);

        // 2. 校验抽奖是否存在 & 只能禁用自己发布的抽奖
        Draw draw = drawMapper.selectById(drawId);
        if (draw == null) {
            throw new BusinessException("抽奖不存在");
        }
        if (!draw.getPublisherUserId().equals(userId)) {
            throw new BusinessException("不能禁用他人的抽奖口令");
        }

        // 3. 核心：删除Redis中的两个key，立即失效口令
        String drawIdKey = prefix + drawId;
        redisTemplate.delete(passCodeKey);  // 删除 口令->抽奖ID
        redisTemplate.delete(drawIdKey);    // 删除 抽奖ID->口令
    }

    @Override
    public PassCodeVO queryPassCode(Long userId, Long drawId) {

        Draw draw = drawMapper.selectById(drawId);
        if(draw == null){
            throw new IllegalArgumentException("抽奖不存在");
        }
        if(!draw.getPublisherUserId().equals(userId)){
            throw new IllegalArgumentException("不可查询他人抽奖的口令");
        }

        // 从redis中读取
        String prefix = RedisConstant.PassCodePrefix;
        String redisKey = prefix + drawId;
        String passcode = redisTemplate.opsForValue().get(redisKey);

        if(passcode == null){
            throw new BusinessException("口令不存在");
        }

        // ========== 核心：计算剩余时间和过期时间戳 ==========
        // 获取剩余过期时间（秒）
        Long remainValidSecond = redisTemplate.getExpire(redisKey);

        // 计算过期时间戳（当前时间 + 剩余秒数 = 毫秒级时间戳）
        long expireTime = System.currentTimeMillis() + remainValidSecond * 1000;

        // 封装VO
        PassCodeVO vo = new PassCodeVO();
        vo.setPassCode(passcode);
        vo.setExpireTime(expireTime);       // 过期时间戳（毫秒）
        vo.setRemainValidSecond(remainValidSecond); // 剩余秒数
        return vo;
    }

    @Override
    public DrawSmallVO queryDrawByPC(Long userId, String passCode) {

        String prefix = RedisConstant.PassCodePrefix;
        String drawString = redisTemplate.opsForValue().get(prefix + passCode);
        if(drawString == null){
            throw new BusinessException("口令已失效");
        }
        Long drawId = Long.valueOf(drawString);

        Draw draw = drawMapper.selectById(drawId);
        if(draw == null){
            throw new IllegalArgumentException("抽奖不存在");
        }
        DrawSmallVO vo = new DrawSmallVO();
        vo.setTitle(draw.getTitle());
        vo.setDrawId(draw.getDrawId());
        vo.setDrawCover(draw.getDrawCover());

        return vo;
    }


    private static int generateSixDigitCode() {
        Random random = new Random();
        // 生成 0~899999，再加 100000，保证6位
        return 100000 + random.nextInt(900000);
    }



}
