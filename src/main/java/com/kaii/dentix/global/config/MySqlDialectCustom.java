package com.kaii.dentix.global.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.StandardBasicTypes;

public class MySqlDialectCustom extends MySQLDialect {

    public MySqlDialectCustom(){
        super();
        FunctionContributions functionContributions = null;

        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        functionRegistry.register("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));

    }

}
