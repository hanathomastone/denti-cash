package com.kaii.dentix.domain.admin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminWalletTransaction is a Querydsl query type for AdminWalletTransaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminWalletTransaction extends EntityPathBase<AdminWalletTransaction> {

    private static final long serialVersionUID = -1971992796L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminWalletTransaction adminWalletTransaction = new QAdminWalletTransaction("adminWalletTransaction");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final QAdminWallet adminWallet;

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath contractAddress = createString("contractAddress");

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final EnumPath<com.kaii.dentix.domain.type.TransactionType> transactionType = createEnum("transactionType", com.kaii.dentix.domain.type.TransactionType.class);

    public QAdminWalletTransaction(String variable) {
        this(AdminWalletTransaction.class, forVariable(variable), INITS);
    }

    public QAdminWalletTransaction(Path<? extends AdminWalletTransaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminWalletTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminWalletTransaction(PathMetadata metadata, PathInits inits) {
        this(AdminWalletTransaction.class, metadata, inits);
    }

    public QAdminWalletTransaction(Class<? extends AdminWalletTransaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.adminWallet = inits.isInitialized("adminWallet") ? new QAdminWallet(forProperty("adminWallet"), inits.get("adminWallet")) : null;
    }

}

