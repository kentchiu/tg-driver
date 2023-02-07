alter table message_video
    modify file_size bigint null;

alter table message_document
    add constraint message_uid_uk
        unique (message_uid);

alter table message_photo
    add constraint message_uid_uk
        unique (message_uid);

alter table message_text
    add constraint message_uid_uk
        unique (message_uid);

alter table message_video
    add constraint message_uid_uk
        unique (message_uid);

