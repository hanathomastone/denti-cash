package com.kaii.dentix.domain.admin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminWallet is a Querydsl query type for AdminWallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminWallet extends EntityPathBase<AdminWallet> {

    private static final long serialVersionUID = 535328602L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminWallet adminWallet = new QAdminWallet("adminWallet");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final BooleanPath active = createBoolean("active");

    public final StringPath address = createString("address");

    public final NumberPath<Long> balance = createNumber("balance", Long.class);

    public final com.kaii.dentix.domain.blockChain.token.domain.QTokenContract contract;

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final StringPath encryptedPrivateKey = createString("encryptedPrivateKey");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public QAdminWallet(String variable) {
        this(AdminWallet.class, forVariable(variable), INITS);
    }

    public QAdminWallet(Path<? extends AdminWallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminWallet(PathMetadata metadata, PathInits inits) {
        this(AdminWallet.class, metadata, inits);
    }

    public QAdminWallet(Class<? extends AdminWallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contract = inits.isInitialized("contract") ? new com.kaii.dentix.domain.blockChain.token.domain.QTokenContract(forProperty("contract")) : null;
    }

}

