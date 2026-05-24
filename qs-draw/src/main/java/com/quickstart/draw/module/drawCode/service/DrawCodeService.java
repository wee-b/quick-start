package com.quickstart.draw.module.drawCode.service;

import com.quickstart.common.domain.drawCode.vo.DrawCodeVO;

import java.util.List;

public interface DrawCodeService {

    List<String> joinDraw(Long drawId, Long userId);

    List<DrawCodeVO> getMyCodes(Long drawId, Long userId);

    void openDraw(Long drawId, Long userId);

//    void handleJoinCodeMessage(Long drawParticipantId, Long drawId, Long userId);

}
