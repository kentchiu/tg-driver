drop table if exists chat;
create table chat
(
    uid            bigint auto_increment primary key,
    chat_id        bigint       not null unique key,
    name           varchar(100) not null,
    disabled       bit default false,
    file_unique_id varchar(30)
);

drop table if exists message;
create table message
(
    uid                 bigint auto_increment primary key,
    chat_id             bigint      not null,
    message_id          bigint      not null,
    date                datetime    null,
    media_album_id      bigint      null,
    message_thread_id   bigint      null,
    reply_to_message_id bigint      null,
    reply_in_chat_id    bigint      null,
    type                varchar(30) not null,
    read_at             datetime    null,
    delete_at           datetime    null,
    constraint message_chat_id_message_id_uindex unique (chat_id, message_id)
);


create index message_message_id_index on message (message_id);
create index message_chat_id_index on message (chat_id);


drop table if exists message_text;
create table message_text
(
    uid         bigint auto_increment primary key,
    message_uid bigint        null,
    text        varchar(2048) null
);

create index message_text_message_uid_index on message_text (message_uid);


drop table if exists message_photo;
create table message_photo
(
    uid            bigint auto_increment primary key,
    message_uid    bigint        null,
    caption        varchar(1000) null,
    file_unique_id varchar(30)   not null,
    width          int           null,
    height         int           null
);

create index message_photo_message_uid_index on message_photo (message_uid);
create index message_photo_file_unique_id_index on message_photo (file_unique_id);


drop table if exists message_video;
create table message_video
(
    uid                bigint auto_increment primary key,
    message_uid        bigint        null,
    caption            varchar(1000) null,
    width              int           null,
    height             int           null,
    duration           int,
    file_size          int,
    file_name          varchar(100),
    mime_type          varchar(20),
    supports_streaming bit,
    file_unique_id     varchar(30)   not null,
    thumbnail_id       varchar(30)   not null comment 'thumbnail file_unique_id'
);

create index message_video_message_uid_index on message_video (message_uid);
create index message_video_file_unique_id_index on message_video (file_unique_id);


drop table if exists message_document;
create table message_document
(
    uid            bigint auto_increment primary key,
    message_uid    bigint        null,
    caption        varchar(1000) null,
    file_name      varchar(100),
    mime_type      varchar(50),
    file_unique_id char(30)      not null
);

create index message_document_message_uid_index on message_document (message_uid);
create index message_document_file_unique_id on message_document (file_unique_id);


drop table if exists file;
create table file
(
    uid             bigint auto_increment primary key,
    file_size       int          null,
    file_unique_id  char(30)     not null unique key,
    local_file_path varchar(100) null,
    last_modified   datetime     not null
);


drop table if exists ban_rule;
create table ban_rule
(
    uid      bigint auto_increment primary key,
    rule     varchar(200) not null unique key,
    property varchar(50)  not null
);


