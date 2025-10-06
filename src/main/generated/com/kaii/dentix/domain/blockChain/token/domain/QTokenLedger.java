package com.kaii.dentix.domain.blockChain.token.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTokenLedger is a Querydsl query type for TokenLedger
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokenLedger extends EntityPathBase<TokenLedger> {

    private static final long serialVersionUID = 1576909418L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTokenLedger tokenLedger = new QTokenLedger("tokenLedger");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final QTokenContract contract;

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final com.kaii.dentix.domain.admin.domain.QAdminWallet fromAdminWallet;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final EnumPath<com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus> status = createEnum("status", com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus.class);

    public final com.kaii.dentix.domain.blockChain.wallet.domain.QUserWallet toUserWallet;

    public final StringPath txHash = createString("txHash");

    public final EnumPath<com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType> type = createEnum("type", com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType.class);

    public QTokenLedger(String variable) {
        this(TokenLedger.class, forVariable(variable), INITS);
    }

    public QTokenLedger(Path<? extends TokenLedger> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTokenLedger(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTokenLedger(PathMetadata metadata, PathInits inits) {
        this(TokenLedger.class, metadata, inits);
    }

    public QTokenLedger(Class<? extends TokenLedger> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contract = inits.isInitialized("contract") ? new QTokenContract(forProperty("contract")) : null;
        this.fromAdminWallet = inits.isInitialized("fromAdminWallet") ? new com.kaii.dentix.domain.admin.domain.QAdminWallet(forProperty("fromAdminWallet"), inits.get("fromAdminWallet")) : null;
        this.toUserWallet = inits.isInitialized("toUserWallet") ? new com.kaii.dentix.domain.blockChain.wallet.domain.QUserWallet(forProperty("toUserWallet"), inits.get("toUserWallet")) : null;
    }

}

