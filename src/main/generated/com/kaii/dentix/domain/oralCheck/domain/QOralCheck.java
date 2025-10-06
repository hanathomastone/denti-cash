package com.kaii.dentix.domain.oralCheck.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOralCheck is a Querydsl query type for OralCheck
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOralCheck extends EntityPathBase<OralCheck> {

    private static final long serialVersionUID = -634813471L;

    public static final QOralCheck oralCheck = new QOralCheck("oralCheck");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckAnalysisState> oralCheckAnalysisState = createEnum("oralCheckAnalysisState", com.kaii.dentix.domain.type.oral.OralCheckAnalysisState.class);

    public final NumberPath<Float> oralCheckDownLeftRange = createNumber("oralCheckDownLeftRange", Float.class);

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckResultType> oralCheckDownLeftScoreType = createEnum("oralCheckDownLeftScoreType", com.kaii.dentix.domain.type.oral.OralCheckResultType.class);

    public final NumberPath<Float> oralCheckDownRightRange = createNumber("oralCheckDownRightRange", Float.class);

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckResultType> oralCheckDownRightScoreType = createEnum("oralCheckDownRightScoreType", com.kaii.dentix.domain.type.oral.OralCheckResultType.class);

    public final NumberPath<Long> oralCheckId = createNumber("oralCheckId", Long.class);

    public final StringPath oralCheckPicturePath = createString("oralCheckPicturePath");

    public final StringPath oralCheckResultJsonData = createString("oralCheckResultJsonData");

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckResultType> oralCheckResultTotalType = createEnum("oralCheckResultTotalType", com.kaii.dentix.domain.type.oral.OralCheckResultType.class);

    public final NumberPath<Float> oralCheckTotalRange = createNumber("oralCheckTotalRange", Float.class);

    public final NumberPath<Float> oralCheckUpLeftRange = createNumber("oralCheckUpLeftRange", Float.class);

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckResultType> oralCheckUpLeftScoreType = createEnum("oralCheckUpLeftScoreType", com.kaii.dentix.domain.type.oral.OralCheckResultType.class);

    public final NumberPath<Float> oralCheckUpRightRange = createNumber("oralCheckUpRightRange", Float.class);

    public final EnumPath<com.kaii.dentix.domain.type.oral.OralCheckResultType> oralCheckUpRightScoreType = createEnum("oralCheckUpRightScoreType", com.kaii.dentix.domain.type.oral.OralCheckResultType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QOralCheck(String variable) {
        super(OralCheck.class, forVariable(variable));
    }

    public QOralCheck(Path<? extends OralCheck> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOralCheck(PathMetadata metadata) {
        super(OralCheck.class, metadata);
    }

}

