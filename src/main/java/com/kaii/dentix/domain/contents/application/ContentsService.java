package com.kaii.dentix.domain.contents.application;

import com.kaii.dentix.domain.contents.dao.ContentsCategoryRepository;
import com.kaii.dentix.domain.contents.dao.ContentsListRepository;
import com.kaii.dentix.domain.contents.dao.ContentsRepository;
import com.kaii.dentix.domain.contents.domain.ContentsList;
import com.kaii.dentix.domain.contents.dto.ContentsCategories;
import com.kaii.dentix.domain.contents.dto.ContentsCategoryListDto;
import com.kaii.dentix.domain.contents.dto.ContentsListDto;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsCategoryRepository contentsCategoryRepository;

    private final ContentsRepository contentsRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    private final ContentsListRepository contentsListRepository;

    public User getTokenUser(HttpServletRequest servletRequest) {
        String token = jwtTokenUtil.getAccessToken(servletRequest);

        if (token == null){
            return null;
        }

        UserRole roles = jwtTokenUtil.getRoles(token, TokenType.AccessToken);
        if (!roles.equals(UserRole.ROLE_USER)) throw new UnauthorizedException("권한이 없는 사용자입니다.");

        Long userId = jwtTokenUtil.getUserId(token, TokenType.AccessToken);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));
    }

    /**
     *  콘텐츠 카테고리 목록 조회
     */
    @Transactional
    public ContentsCategoryListDto contentsCategoryList(HttpServletRequest httpServletRequest){

        // 미로그인 사용자
        List<ContentsCategories> categoryList = contentsCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "contentsCategorySort")).stream()
                .map(contentsCategory ->
                        ContentsCategories.builder()
                                .contentsCategoryId(contentsCategory.getContentsCategoryId())
                                .contentsCategorySort(contentsCategory.getContentsCategorySort())
                                .contentsCategoryName(contentsCategory.getContentsCategoryName())
                                .contentsCategoryColor(contentsCategory.getContentsCategoryColor())
                                .build()
                ).toList();

        List<ContentsListDto> contentsListDto = contentsRepository.findAll(Sort.by(Sort.Direction.ASC, "contentsSort")).stream()
                .map(contents -> {
                    List<ContentsList> contentsLists = contentsListRepository.findByContentsId(contents.getContentsId());
                    return ContentsListDto.builder()
                            .contentsId(contents.getContentsId())
                            .contentsTitle(contents.getContentsTitle())
                            .contentsSort(contents.getContentsSort())
                            .contentsType(contents.getContentsType())
                            .contentsTitleColor(contents.getContentsTitleColor())
                            .contentsThumbnail(contents.getContentsThumbnail())
                            .contentsPath(contents.getContentsPath())
                            .contentsLists(contentsLists)
                            .build();
                        }
                ).toList();

        // 로그인 한 사용자
        if (this.getTokenUser(httpServletRequest) != null){
            User user = this.getTokenUser(httpServletRequest);

            // 인증된 사용자
            if (user.getPatientId() != null) {
                List<ContentsCategories> newCategoryList = new ArrayList<>(categoryList);
                ContentsCategories userCategory = ContentsCategories.builder()
                        .contentsCategoryId(0L)
                        .contentsCategorySort(0)
                        .contentsCategoryName(user.getUserName() + "님 맞춤")
                        .contentsCategoryColor(null)
                        .build();
                newCategoryList.add(0, userCategory);

                List<ContentsListDto> newwCategoryList = new ArrayList<>(contentsListDto);

                return new ContentsCategoryListDto(newCategoryList, newwCategoryList);
            }
        }
        return new ContentsCategoryListDto(categoryList, contentsListDto);
    }

}
