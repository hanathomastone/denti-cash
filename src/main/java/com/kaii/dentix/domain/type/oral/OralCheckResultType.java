package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum OralCheckResultType {

    // 구강 검사 결과 종합

    HEALTHY(0, "건강", "전체적으로 건강한 상태", 10),
    GOOD(1, "양호", "전체적으로 양호한 상태", 5),
    ATTENTION(2, "주의", "전체적으로 주의가 필요한 상태", 3),
    DANGER(3, "위험", "전체적으로 위험한 상태", 1);

    private final int id;
    private final String type; // 유형
    private final String doctorComment;
    private final long reward;// Comment

    OralCheckResultType(int id, String type, String doctorComment, long reward) {
        this.id = id;
        this.type = type;
        this.doctorComment = doctorComment;
        this.reward = reward;
    }

    public long getReward() {
        return reward;
    }

}
