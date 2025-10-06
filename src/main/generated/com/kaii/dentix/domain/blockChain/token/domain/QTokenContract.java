package com.kaii.dentix.domain.blockChain.token.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTokenContract is a Querydsl query type for TokenContract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokenContract extends EntityPathBase<TokenContract> {

    private static final long serialVersionUID = 1364845011L;

    public static final QTokenContract tokenContract = new QTokenContract("tokenContract");

    public final BooleanPath active = createBoolean("active");

    public final StringPath contractAddress = createString("contractAddress");

    public final NumberPath<Integer> decimals = createNumber("decimals", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QTokenContract(String variable) {
        super(TokenContract.class, forVariable(variable));
    }

    public QTokenContract(Path<? extends TokenContract> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTokenContract(PathMetadata metadata) {
        super(TokenContract.class, metadata);
    }

}

