
    create table cn_attachment (
       id varchar(36) not null,
        attachment_id varchar(255),
        filename varchar(255),
        version integer,
        message_id varchar(36),
        primary key (id)
    );

    create table cn_broker (
       id varchar(36) not null,
        created_at timestamp,
        email varchar(255),
        enable integer,
        refresh_token varchar(2048),
        type varchar(255),
        version integer,
        user_id varchar(36),
        primary key (id)
    );

    create table cn_file (
       file blob,
        attachment_id varchar(36) not null,
        primary key (attachment_id)
    );

    create table cn_message (
       id varchar(36) not null,
        message_date timestamp,
        message_id varchar(255),
        version integer,
        broker_id varchar(36),
        primary key (id)
    );

    create table cn_send_status (
       status varchar(255),
        version integer,
        attachment_id varchar(36) not null,
        primary key (attachment_id)
    );

    create table cn_user (
       id varchar(36) not null,
        created_at timestamp,
        identity_id varchar(255),
        provider varchar(255),
        token varchar(2048),
        username varchar(255),
        version integer,
        primary key (id)
    );

    alter table cn_message
       add constraint UKnsh4k6r59xwgh1b6e7hi5gssl unique (message_id, broker_id);

    alter table cn_user
       add constraint UK5wtbbbk9glk087qucfky5sfu9 unique (username);

    alter table cn_user
       add constraint UKdo3rrt9o142kgb3m9nhkt8pp7 unique (identity_id);

    alter table cn_attachment
       add constraint FKdun7grupod0nmkh6l8h2dt8m4
       foreign key (message_id)
       references cn_message;

    alter table cn_broker
       add constraint FKiukbyru8q5id0pvx60mahiwym
       foreign key (user_id)
       references cn_user;

    alter table cn_file
       add constraint FKax7vlxnpr0i9n3s54hpxyakjg
       foreign key (attachment_id)
       references cn_attachment;

    alter table cn_message
       add constraint FKpqhxyb0354pe4erhm9852fo1w
       foreign key (broker_id)
       references cn_broker;

    alter table cn_send_status
       add constraint FKny5251ywk1uxc73sw2dsh297u
       foreign key (attachment_id)
       references cn_attachment;
