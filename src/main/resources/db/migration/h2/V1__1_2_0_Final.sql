
    create table cn_file (
       id varchar(36) not null,
        created_at timestamp,
        file blob,
        filename varchar(255),
        updated_at timestamp,
        version integer,
        group_id varchar(36),
        message_id varchar(36),
        primary key (id)
    );

    create table cn_group_file (
       id varchar(36) not null,
        sended char(255),
        version integer,
        primary key (id)
    );

    create table cn_linked_broker (
       id varchar(36) not null,
        created_at timestamp,
        email varchar(255),
        last_time_synchronized timestamp,
        token varchar(2048),
        type varchar(255),
        updated_at timestamp,
        version integer,
        user_id varchar(36),
        primary key (id)
    );

    create table cn_message (
       id varchar(36) not null,
        internal_date timestamp,
        message_id varchar(255),
        broker_id varchar(36),
        primary key (id)
    );

    create table cn_send_event (
       id varchar(36) not null,
        message varchar(255),
        result varchar(255),
        send_date timestamp,
        send_event_id varchar(36),
        primary key (id)
    );

    create table cn_user (
       id varchar(36) not null,
        created_at timestamp,
        identity_id varchar(255),
        provider varchar(255),
        registration_complete char(255),
        token varchar(2048),
        updated_at timestamp,
        username varchar(255),
        version integer,
        primary key (id)
    );

    alter table cn_linked_broker
       add constraint UKb2koytpmq08gmp2lksj7nncri unique (email);

    alter table cn_message
       add constraint UKnsh4k6r59xwgh1b6e7hi5gssl unique (message_id, broker_id);

    alter table cn_user
       add constraint UK5wtbbbk9glk087qucfky5sfu9 unique (username);

    alter table cn_user
       add constraint UKdo3rrt9o142kgb3m9nhkt8pp7 unique (identity_id);

    alter table cn_file
       add constraint FKdt3wslufxn1qogxjypwfgypty
       foreign key (group_id)
       references cn_group_file;

    alter table cn_file
       add constraint FK9lvkcradwjxooblbkqt6xyv6i
       foreign key (message_id)
       references cn_message;

    alter table cn_linked_broker
       add constraint FKigmru3u703l27l0a09be9jrd5
       foreign key (user_id)
       references cn_user;

    alter table cn_message
       add constraint FKdgm5mvumt0403ll722udw9v9q
       foreign key (broker_id)
       references cn_linked_broker;

    alter table cn_send_event
       add constraint FKogh6k8r4klf06mrqyd6tn8kfo
       foreign key (send_event_id)
       references cn_group_file;
