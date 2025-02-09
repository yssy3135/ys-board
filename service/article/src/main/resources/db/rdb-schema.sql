create table article (
    article_id bigint not null primary key,
    title varchar(100) not null,
    content varchar(3000) not null,
    board_id bigint not null,
    writer_id bigint not null,
    created_at datetime not null,
    modified_at datetime not null
);


create database comment;
use comment;
create table comment (
     comment_id bigint not null primary key,
     content varchar(3000) not null,
     article_id bigint not null,
     parent_comment_id bigint not null,
     writer_id bigint not null,
     deleted bool not null,
     created_at datetime not null
);