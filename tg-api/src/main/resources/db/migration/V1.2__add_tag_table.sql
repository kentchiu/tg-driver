drop table if exists tag;
create table tag
(
    uid         bigint auto_increment primary key,
    name        varchar(100) not null unique key,
    description varchar(255)
);


INSERT INTO tag (uid, name, description)
VALUES (1, 'WATCHED', 'Video already be played or be watched or be marked as watched');
INSERT INTO tag (uid, name, description)
VALUES (2, 'PINNED', 'Pinned Video will not be deleted and will ignore WATCHED tag');
