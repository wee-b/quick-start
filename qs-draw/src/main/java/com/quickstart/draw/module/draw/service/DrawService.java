package com.quickstart.draw.module.draw.service;

import com.quickstart.common.domain.draw.Draw;
import com.quickstart.common.domain.draw.dto.DrawCreateRequest;
import com.quickstart.common.domain.draw.dto.GenerateCodeDTO;
import com.quickstart.common.domain.draw.vo.*;

import java.util.List;

public interface DrawService {

    List<DrawSmallVO> getOfficialDraw();

    DrawVO createDraw(DrawCreateRequest request, Long publisherUserId);

    DrawVO getDetailDraw(Long drawId);

    DrawVO updateDraw(DrawCreateRequest request, Long userId);

    void deleteDraw(Long drawId, Long userId);

    void publishDraw(Long drawId, Long userId);


    List<Draw> listExpiredRunningDraws();


    void generatePassCode(Long userId, GenerateCodeDTO dto);

    void banPassCode(Long userId, String passCode);

    PassCodeVO queryPassCode(Long userId, Long drawId);

    DrawSmallVO queryDrawByPC(Long userId, String passCode);



}
