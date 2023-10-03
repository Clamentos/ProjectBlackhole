package io.github.clamentos.blackhole.persistence.models;

import java.util.List;

public record UserEntity(

    int id,
    short flags,
    short report_count,
    short login_failure_count,
    String username,
    String email,
    String password_hash,
    int creation_date,
    int last_modified,
    String about,

    RoleEntity role,
    AvatarEntity avatar,
    List<ReportEntity> reports
) {}
