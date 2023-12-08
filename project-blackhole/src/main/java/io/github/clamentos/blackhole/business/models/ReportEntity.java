package io.github.clamentos.blackhole.business.models;

import java.util.List;

public record ReportEntity(

    long id,
    int creation_date,
    String explanation,

    ReportTypeEntity report_type,
    List<UserEntity> issuer

) {}
