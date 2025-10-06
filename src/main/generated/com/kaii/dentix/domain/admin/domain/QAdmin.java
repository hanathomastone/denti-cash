package com.kaii.dentix.domain.admin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdmin is a Querydsl query type for Admin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdmin extends EntityPathBase<Admin> {

    private static final long serialVersionUID = 502266881L;

    public static final QAdmin admin = new QAdmin("admin");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final NumberPath<Long> adminId = createNumber("adminId", Long.class);

    public final EnumPath<com.kaii.dentix.domain.type.YnType> adminIsSuper = createEnum("adminIsSuper", com.kaii.dentix.domain.type.YnType.class);

    public final DateTimePath<java.util.Date> adminLastLoginDate = createDateTime("adminLastLoginDate", java.util.Date.class);

    public final StringPath adminLoginIdentifier = createString("adminLoginIdentifier");

    public final StringPath adminName = createString("adminName");

    public final StringPath adminPassword = createString("adminPassword");

    public final StringPath adminPhoneNumber = createString("adminPhoneNumber");

    public final StringPath adminRefreshToken = createString("adminRefreshToken");

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final DateTimePath<java.util.Date> deleted = createDateTime("deleted", java.util.Date.class);

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public QAdmin(String variable) {
        super(Admin.class, forVariable(variable));
    }

    public QAdmin(Path<? extends Admin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdmin(PathMetadata metadata) {
        super(Admin.class, metadata);
    }

}

