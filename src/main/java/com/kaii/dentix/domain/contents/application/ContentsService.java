package com.kaii.dentix.domain.contents.application;

import com.kaii.dentix.domain.contents.dao.ContentsCardRepository;
import com.kaii.dentix.domain.contents.dao.ContentsCategoryRepository;
import com.kaii.dentix.domain.contents.dao.ContentsListRepository;
import com.kaii.dentix.domain.contents.dao.ContentsRepository;
import com.kaii.dentix.domain.contents.domain.Contents;
import com.kaii.dentix.domain.contents.domain.ContentsToCategory;
import com.kaii.dentix.domain.contents.dto.*;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsCategoryRepository contentsCategoryRepository;

    private final ContentsRepository contentsRepository;

    private final UserService userService;

    private final ContentsListRepository contentsListRepository;

    private final ContentsCardRepository contentsCardRepository;

    /**
     *  콘텐츠 조회
     */
    @Transactional
    public ContentsListDto contentsList(HttpServletRequest httpServletRequest){

        // 카테고리 리스트 - 비로그인 사용자의 경우
        List<ContentsCategoryDto> categoryList = contentsCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "contentsCategorySort")).stream()
                .map(contentsCategory ->
                        ContentsCategoryDto.builder()
                                .id(contentsCategory.getContentsCategoryId())
                                .sort(contentsCategory.getContentsCategorySort())
                                .name(contentsCategory.getContentsCategoryName())
                                .color(contentsCategory.getContentsCategoryColor())
                                .build()
                ).toList();

        // 콘텐츠 리스트 - 비로그인 사용자의 경우
        List<ContentsDto> contentsDto = contentsRepository.findAll(Sort.by(Sort.Direction.ASC, "contentsSort")).stream()
                .map(contents -> {
                    // 콘텐츠 별 카테고리 Id 조회
                    List<Integer> contentsLists = contentsListRepository.findByContentsId(contents.getContentsId()).stream()
                            .map(ContentsToCategory::getContentsCategoryId)
                            .collect(Collectors.toList());

                    return new ContentsDto(
                            contents.getContentsId(),
                            contents.getContentsTitle(),
                            contents.getContentsSort(),
                            contents.getContentsType(),
                            contents.getContentsTypeColor(),
                            contents.getContentsThumbnail(),
                            contents.getContentsPath(),
                            contentsLists);
                }).toList();

        User user = userService.getTokenUserNullable(httpServletRequest);

        // 로그인 한 사용자
        if (user != null){

            // 사용자 맞춤 카테고리 추가 - 인증된 사용자의 경우
            if (user.getPatientId() != null) {
                List<ContentsCategoryDto> userCategoryList = new ArrayList<>(categoryList);
                ContentsCategoryDto userCategory = ContentsCategoryDto.builder()
                        .id(0)
                        .sort(1)
                        .name(user.getUserName() + "님 맞춤")
                        .color(null)
                        .build();
                userCategoryList.add(0, userCategory);

                for (int i = 0; i < userCategoryList.size(); i++) {
                    userCategoryList.get(i).setSort(i + 1);
                }

                List<ContentsDto> userContentsList = new ArrayList<>(contentsDto);

                return new ContentsListDto(userCategoryList, userContentsList);

                // TODO : 사용자 맞춤 콘텐츠 리스트 조회
            }
        }
        return new ContentsListDto(categoryList, contentsDto);
    }

    /**
     *  콘텐츠 카드뉴스
     */
    @Transactional
    public ContentsCardListDto contentsCard(Long contentsId){
        Contents contents = contentsRepository.findById(contentsId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 콘텐츠입니다."));

        List<ContentsCardDto> contentsCardList = contentsCardRepository.findAllByContentsId(contents.getContentsId()).stream()
                .map(contentsList -> new ContentsCardDto(contentsList.getContentsCardNumber(), contentsList.getContentsCardPath()))
                .collect(Collectors.toList());

        return new ContentsCardListDto(contents.getContentsTitle(), contentsCardList);
    }

}
