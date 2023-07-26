package com.kaii.dentix.domain.toothBrushing.application;

import com.kaii.dentix.domain.toothBrushing.dao.ToothBrushingRepository;
import com.kaii.dentix.domain.toothBrushing.domain.ToothBrushing;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDto;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingRegisterDto;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.util.DateFormatUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToothBrushingService {

    private final UserService userService;

    private final ToothBrushingRepository toothBrushingRepository;

    /**
     *  양치질 기록
     */
    @Transactional(rollbackFor = Exception.class)
    public ToothBrushingRegisterDto toothBrushing(HttpServletRequest httpServletRequest){
        User user = userService.getTokenUser(httpServletRequest);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        List<ToothBrushing> toothBrushingList = toothBrushingRepository.findByUserIdAndCreatedOrderByCreated(user.getUserId(), DateFormatUtil.dateToString("yyyy-MM-dd", today));

        if (toothBrushingList.size() >= 3) {
            throw new BadRequestApiException("오늘의 양치는 이미 완료했어요.");
        }

        if (toothBrushingList.size() > 0) {
            Date latestToothBrushingCreated = toothBrushingList.get(toothBrushingList.size() - 1).getCreated();
            calendar.add(Calendar.MINUTE, -1);

            // 양치한지 아직 1시간이 되지 않은 경우
            if (latestToothBrushingCreated.after(calendar.getTime())) {
                return ToothBrushingRegisterDto.builder()
                    .timeInterval((today.getTime() - latestToothBrushingCreated.getTime()) / 1000)
                    .build();
            }
        }
        ToothBrushing toothBrushing = toothBrushingRepository.save(ToothBrushing.builder()
                .userId(user.getUserId())
                .build());

        toothBrushingList.add(toothBrushing);

        return ToothBrushingRegisterDto.builder()
                .toothBrushingList(toothBrushingList.stream()
                    .map(t -> new ToothBrushingDto(t.getToothBrushingId(), t.getCreated()))
                    .toList())
                .build();
    }

}
