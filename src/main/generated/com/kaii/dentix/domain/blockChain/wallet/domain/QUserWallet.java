package com.kaii.dentix.domain.blockChain.wallet.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserWallet is a Querydsl query type for UserWallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserWallet extends EntityPathBase<UserWallet> {

    private static final long serialVersionUID = 420614742L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserWallet userWallet = new QUserWallet("userWallet");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<Long> balance = createNumber("balance", Long.class);

    public final com.kaii.dentix.domain.blockChain.token.domain.QTokenContract contract;

    public final StringPath encryptedPrivateKey = createString("encryptedPrivateKey");

    public final com.kaii.dentix.domain.user.domain.QUser user;

    public final NumberPath<Long> userWalletId = createNumber("userWalletId", Long.class);

    public final StringPath walletAddress = createString("walletAddress");

    public QUserWallet(String variable) {
        this(UserWallet.class, forVariable(variable), INITS);
    }

    public QUserWallet(Path<? extends UserWallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserWallet(PathMetadata metadata, PathInits inits) {
        this(UserWallet.class, metadata, inits);
    }

    public QUserWallet(Class<? extends UserWallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contract = inits.isInitialized("contract") ? new com.kaii.dentix.domain.blockChain.token.domain.QTokenContract(forProperty("contract")) : null;
        this.user = inits.isInitialized("user") ? new com.kaii.dentix.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

