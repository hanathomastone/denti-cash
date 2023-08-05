package com.kaii.dentix.domain.questionnaire.dto.request;

import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireKeyValueDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireSubmitRequest {

    @NotNull(message = "문진표 작성은 필수입니다.")
    private List<QuestionnaireKeyValueDto> form;
}
