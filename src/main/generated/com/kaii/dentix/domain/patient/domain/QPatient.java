package com.kaii.dentix.domain.patient.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPatient is a Querydsl query type for Patient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPatient extends EntityPathBase<Patient> {

    private static final long serialVersionUID = 446540161L;

    public static final QPatient patient = new QPatient("patient");

    public final com.kaii.dentix.global.common.entity.QTimeEntity _super = new com.kaii.dentix.global.common.entity.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final DateTimePath<java.util.Date> deleted = createDateTime("deleted", java.util.Date.class);

    //inherited
    public final DateTimePath<java.util.Date> modified = _super.modified;

    public final NumberPath<Long> patientId = createNumber("patientId", Long.class);

    public final StringPath patientName = createString("patientName");

    public final StringPath patientPhoneNumber = createString("patientPhoneNumber");

    public QPatient(String variable) {
        super(Patient.class, forVariable(variable));
    }

    public QPatient(Path<? extends Patient> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPatient(PathMetadata metadata) {
        super(Patient.class, metadata);
    }

}

