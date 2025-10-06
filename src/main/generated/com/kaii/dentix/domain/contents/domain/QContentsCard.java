package com.kaii.dentix.domain.contents.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QContentsCard is a Querydsl query type for ContentsCard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentsCard extends EntityPathBase<ContentsCard> {

    private static final long serialVersionUID = -1357011075L;

    public static final QContentsCard contentsCard = new QContentsCard("contentsCard");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final NumberPath<Long> contentsCardId = createNumber("contentsCardId", Long.class);

    public final NumberPath<Integer> contentsCardNumber = createNumber("contentsCardNumber", Integer.class);

    public final StringPath contentsCardPath = createString("contentsCardPath");

    public final NumberPath<Integer> contentsId = createNumber("contentsId", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public QContentsCard(String variable) {
        super(ContentsCard.class, forVariable(variable));
    }

    public QContentsCard(Path<? extends ContentsCard> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContentsCard(PathMetadata metadata) {
        super(ContentsCard.class, metadata);
    }

}

