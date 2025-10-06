package com.kaii.dentix.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2102436177L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final DatePath<java.time.LocalDate> birth = createDate("birth", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final DateTimePath<java.util.Date> deleted = createDateTime("deleted", java.util.Date.class);

    public final StringPath findPwdAnswer = createString("findPwdAnswer");

    public final NumberPath<Long> findPwdQuestionId = createNumber("findPwdQuestionId", Long.class);

    public final EnumPath<com.kaii.dentix.domain.type.YnType> isVerify = createEnum("isVerify", com.kaii.dentix.domain.type.YnType.class);

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final NumberPath<Long> patientId = createNumber("patientId", Long.class);

    public final StringPath userAppVersion = createString("userAppVersion");

    public final StringPath userDeviceManufacturer = createString("userDeviceManufacturer");

    public final StringPath userDeviceModel = createString("userDeviceModel");

    public final StringPath userDeviceToken = createString("userDeviceToken");

    public final NumberPath<Long> userDeviceTypeId = createNumber("userDeviceTypeId", Long.class);

    public final EnumPath<com.kaii.dentix.domain.type.GenderType> userGender = createEnum("userGender", com.kaii.dentix.domain.type.GenderType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final DateTimePath<java.util.Date> userLastLoginDate = createDateTime("userLastLoginDate", java.util.Date.class);

    public final StringPath userLoginIdentifier = createString("userLoginIdentifier");

    public final StringPath userName = createString("userName");

    public final StringPath userOsVersion = createString("userOsVersion");

    public final StringPath userPassword = createString("userPassword");

    public final StringPath userRefreshToken = createString("userRefreshToken");

    public final com.kaii.dentix.domain.blockChain.wallet.domain.QUserWallet userWallet;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userWallet = inits.isInitialized("userWallet") ? new com.kaii.dentix.domain.blockChain.wallet.domain.QUserWallet(forProperty("userWallet"), inits.get("userWallet")) : null;
    }

}

