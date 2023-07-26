package com.kaii.dentix.domain.questionnaire.dto.request;

import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireFormDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireSubmitRequest {

    @NotNull
    private QuestionnaireFormDto form;
}
