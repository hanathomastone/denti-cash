package com.kaii.dentix.domain.toothBrushing.application;

import com.kaii.dentix.domain.toothBrushing.dao.ToothBrushingRepository;
import com.kaii.dentix.domain.toothBrushing.domain.ToothBrushing;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDto;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class ToothBrushingService {

    private final UserService userService;

    private final ToothBrushingRepository toothBrushingRepository;

    /**
     *  양치질 기록
     */
    public ToothBrushingDto toothBrushing(HttpServletRequest httpServletRequest){
        User user = userService.getTokenUser(httpServletRequest);

        ToothBrushing toothBrushing = toothBrushingRepository.save(ToothBrushing.builder()
                .userId(user.getUserId())
                .build());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        return ToothBrushingDto.builder()
                .toothBrushingId(toothBrushing.getToothBrushingId())
                .created(formatter.format(toothBrushing.getCreated()))
                .build();
    }

}
