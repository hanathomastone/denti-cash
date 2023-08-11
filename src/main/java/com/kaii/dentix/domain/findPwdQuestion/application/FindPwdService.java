package com.kaii.dentix.domain.findPwdQuestion.application;

import com.kaii.dentix.domain.findPwdQuestion.dao.FindPwdQuestionRepository;
import com.kaii.dentix.domain.findPwdQuestion.dto.FindPwdQuestionListDto;
import com.kaii.dentix.domain.findPwdQuestion.dto.UserFindPwdQuestionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPwdService {

    private final FindPwdQuestionRepository findPwdQuestionRepository;

    /**
     *  사용자 바밀번호 찾기 질문 리스트
     */
    public FindPwdQuestionListDto userFindPwdQuestions(){

        List<UserFindPwdQuestionsDto> findPwdQuestions = findPwdQuestionRepository.findAll(Sort.by(Sort.Direction.ASC, "findPwdQuestionSort")).stream()
                .map(findPwdQuestion ->
                        UserFindPwdQuestionsDto.builder()
                                .id(findPwdQuestion.getFindPwdQuestionId())
                                .sort(findPwdQuestion.getFindPwdQuestionSort())
                                .title(findPwdQuestion.getFindPwdQuestionTitle())
                                .build()
                ).toList();


        return new FindPwdQuestionListDto(findPwdQuestions);

    }

}
