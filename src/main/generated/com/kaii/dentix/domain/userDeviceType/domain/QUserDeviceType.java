package com.kaii.dentix.domain.userDeviceType.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserDeviceType is a Querydsl query type for UserDeviceType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDeviceType extends EntityPathBase<UserDeviceType> {

    private static final long serialVersionUID = 2093809551L;

    public static final QUserDeviceType userDeviceType1 = new QUserDeviceType("userDeviceType1");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final EnumPath<com.kaii.dentix.domain.type.DeviceType> userDeviceType = createEnum("userDeviceType", com.kaii.dentix.domain.type.DeviceType.class);

    public final NumberPath<Long> userDeviceTypeId = createNumber("userDeviceTypeId", Long.class);

    public final StringPath userDeviceTypeMinVersion = createString("userDeviceTypeMinVersion");

    public QUserDeviceType(String variable) {
        super(UserDeviceType.class, forVariable(variable));
    }

    public QUserDeviceType(Path<? extends UserDeviceType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserDeviceType(PathMetadata metadata) {
        super(UserDeviceType.class, metadata);
    }

}

