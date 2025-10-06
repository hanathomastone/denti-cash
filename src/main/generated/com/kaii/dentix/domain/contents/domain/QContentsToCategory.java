package com.kaii.dentix.domain.contents.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QContentsToCategory is a Querydsl query type for ContentsToCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentsToCategory extends EntityPathBase<ContentsToCategory> {

    private static final long serialVersionUID = -1716131002L;

    public static final QContentsToCategory contentsToCategory = new QContentsToCategory("contentsToCategory");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final NumberPath<Integer> contentsCategoryId = createNumber("contentsCategoryId", Integer.class);

    public final NumberPath<Integer> contentsId = createNumber("contentsId", Integer.class);

    public final NumberPath<Long> contentsToCategoryId = createNumber("contentsToCategoryId", Long.class);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public QContentsToCategory(String variable) {
        super(ContentsToCategory.class, forVariable(variable));
    }

    public QContentsToCategory(Path<? extends ContentsToCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContentsToCategory(PathMetadata metadata) {
        super(ContentsToCategory.class, metadata);
    }

}

