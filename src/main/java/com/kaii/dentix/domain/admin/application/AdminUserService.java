package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.user.UserCustomRepository;
import com.kaii.dentix.domain.admin.dto.AdminUserInfoDto;
import com.kaii.dentix.domain.admin.dto.AdminUserListDto;
import com.kaii.dentix.domain.admin.dto.request.AdminUserListRequest;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.dto.PagingDTO;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    private final UserCustomRepository userCustomRepository;

    private final ModelMapper modelMapper;

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

    /**
     *  사용자 목록 조회
     */
    public AdminUserListDto userList(AdminUserListRequest request){
        Page<AdminUserInfoDto> userList = userCustomRepository.findAll(request);

        PagingDTO pagingDTO = modelMapper.map(userList, PagingDTO.class);

        return AdminUserListDto.builder()
                .paging(pagingDTO)
                .userList(userList.getContent())
                .build();
    }

}
