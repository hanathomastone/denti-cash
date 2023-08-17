package com.kaii.dentix.domain.admin.admin.application;

import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     *  사용자 인증
     */
    @Transactional
    public void userVerify(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));

        if (user.getIsVerify().equals(YnType.Y)) throw new BadRequestApiException("이미 인증된 사용자입니다.");

        user.setIsVerify(YnType.Y);
    }

    /**
     *  사용자 삭제
     */
    @Transactional
    public void userDelete(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));

        user.revoke();
    }

}
