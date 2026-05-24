package com.quickstart.draw.module.draw.controller;

import com.quickstart.common.annotation.NoNeedLogin;
import com.quickstart.common.domain.LoginUser;
import com.quickstart.common.domain.ResponseDTO;
import com.quickstart.common.domain.draw.dto.DrawCreateRequest;
import com.quickstart.common.domain.draw.dto.GenerateCodeDTO;
import com.quickstart.common.domain.draw.vo.*;
import com.quickstart.common.security.SecurityUserContext;
import com.quickstart.draw.module.draw.service.DrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@Tag(name = "抽签模块Draw")
@RestController
public class DrawController {
    @Resource
    private DrawService drawService;

    @GetMapping("/client/draw/getOfficialDraw")
    @Operation(summary = "获取官方抽奖列表")
    @NoNeedLogin
    public ResponseDTO<List<DrawSmallVO>> getOfficialDraw(){
        log.info("收到请求：/client/draw/getOfficialDraw");
        List<DrawSmallVO> res = drawService.getOfficialDraw();
        return ResponseDTO.ok(res);
    }

    @GetMapping("/client/draw/detail")
    @Operation(summary = "获取抽签详情")
    public ResponseDTO<DrawVO> getDetailDraw(@RequestParam("drawId") Long drawId) {
        log.info("收到请求：/client/draw/detail,drawId:{}",drawId);
        DrawVO vo = drawService.getDetailDraw(drawId);
        return ResponseDTO.ok(vo);
    }

    @PostMapping("/client/draw/create")
    @Operation(summary = "发布抽签")
    public ResponseDTO<DrawVO> createDraw(@RequestBody @Valid DrawCreateRequest request) {
        log.info("收到请求：/client/draw/create");
        LoginUser loginUser = SecurityUserContext.getCurrentLoginUser();
        return ResponseDTO.ok(drawService.createDraw(request, loginUser.getUserId()));
    }

    @PostMapping("/client/draw/update")
    @Operation(summary = "修改抽签信息")
    public ResponseDTO<DrawVO> updateDraw(@RequestBody @Valid DrawCreateRequest request) {
        log.info("收到请求：/client/draw/update");
        LoginUser loginUser = SecurityUserContext.getCurrentLoginUser();
        DrawVO vo = drawService.updateDraw(request, loginUser.getUserId());
        return ResponseDTO.ok(vo);
    }

    @PostMapping("/client/draw/delete")
    @Operation(summary = "删除抽签")
    public ResponseDTO<Void> deleteDraw(@RequestParam("drawId") Long drawId) {
        log.info("收到请求：/client/draw/delete");
        LoginUser loginUser = SecurityUserContext.getCurrentLoginUser();
        drawService.deleteDraw(drawId, loginUser.getUserId());
        return ResponseDTO.ok();
    }

    @PostMapping("/client/draw/publish")
    @Operation(summary = "发布抽签")
    public ResponseDTO<Void> publishDraw(@RequestParam("drawId") Long drawId) {
        log.info("收到请求：/client/draw/publish,drawId:{}", drawId);
        LoginUser loginUser = SecurityUserContext.getCurrentLoginUser();
        drawService.publishDraw(drawId, loginUser.getUserId());
        return ResponseDTO.ok();
    }



    @PostMapping("/client/draw/generatePassCode")
    @Operation(summary = "生成口令")
    public ResponseDTO<Void> generatePassCode(@RequestBody @Valid GenerateCodeDTO dto){
        log.info("收到请求：/client/draw/generatePassCode");
        Long userId = SecurityUserContext.getCurrentLoginUser().getUserId();
        drawService.generatePassCode(userId,dto);
        return ResponseDTO.ok();
    }

    @PostMapping("/client/draw/banPassCode")
    @Operation(summary = "禁用口令")
    public ResponseDTO<Void> banPassCode(@RequestParam("passCode") String passCode){
        log.info("收到请求：/client/draw/banPassCode");
        Long userId = SecurityUserContext.getCurrentLoginUser().getUserId();
        drawService.banPassCode(userId,passCode);
        return ResponseDTO.ok();
    }

    @GetMapping("/client/draw/queryPassCode")
    @Operation(summary = "查询口令")
    public ResponseDTO<PassCodeVO> queryPassCode(@RequestParam("drawId") Long drawId){
        log.info("收到请求：/client/draw/queryPassCode,drawId:{}",drawId);
        Long userId = SecurityUserContext.getCurrentLoginUser().getUserId();
        PassCodeVO vo = drawService.queryPassCode(userId,drawId);
        return ResponseDTO.ok(vo);
    }


    @GetMapping("/client/draw/queryDrawByPC")
    @Operation(summary = "根据口令查询抽奖")
    public ResponseDTO<DrawSmallVO> queryDrawByPC(@RequestParam("passCode") String passCode){
        log.info("收到请求：/client/draw/queryDrawByPC,passCode:{}",passCode);
        Long userId = SecurityUserContext.getCurrentLoginUser().getUserId();
        DrawSmallVO vo = drawService.queryDrawByPC(userId,passCode);
        return ResponseDTO.ok(vo);
    }




}