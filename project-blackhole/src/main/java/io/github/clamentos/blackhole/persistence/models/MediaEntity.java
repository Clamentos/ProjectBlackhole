package io.github.clamentos.blackhole.persistence.models;

import java.util.List;
import org.postgresql.core.Oid;

public record MediaEntity(

    long id,
    int creation_date,
    int last_modified,
    short version,
    short report_count,
    byte status,
    String name,
    int upvotes,
    int downvotes,
    Oid data,

    TypeEntity type,
    UserEntity owner,

    List<TagEntity> tags,
    List<UpdateNoteEntity> update_notes,
    List<ReportEntity> reports

) {}
