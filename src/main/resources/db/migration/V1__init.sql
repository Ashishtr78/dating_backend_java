create table users (
    id              uuid primary key,
    email           varchar(254)     not null unique,
    password_hash   varchar(100)     not null,
    display_name    varchar(50)      not null,
    birth_date      date             not null,
    gender          varchar(20)      not null,
    interested_in   varchar(20)      not null,
    bio             varchar(500),
    latitude        double precision,
    longitude       double precision,
    min_age         integer          not null,
    max_age         integer          not null,
    max_distance_km integer          not null,
    created_at      timestamptz      not null,
    updated_at      timestamptz      not null
);

create table user_photos (
    user_id    uuid         not null references users (id) on delete cascade,
    sort_order integer      not null,
    url        varchar(500) not null,
    primary key (user_id, sort_order)
);

create table refresh_tokens (
    id         uuid primary key,
    user_id    uuid        not null references users (id) on delete cascade,
    token_hash varchar(64) not null unique,
    expires_at timestamptz not null,
    created_at timestamptz not null
);
create index idx_refresh_tokens_user on refresh_tokens (user_id);

create table swipes (
    swiper_id  uuid        not null references users (id) on delete cascade,
    target_id  uuid        not null references users (id) on delete cascade,
    liked      boolean     not null,
    created_at timestamptz not null,
    primary key (swiper_id, target_id)
);
create index idx_swipes_target on swipes (target_id);

create table matches (
    id         uuid primary key,
    user_a     uuid        not null references users (id) on delete cascade,
    user_b     uuid        not null references users (id) on delete cascade,
    created_at timestamptz not null,
    unique (user_a, user_b),
    check (user_a < user_b)
);
create index idx_matches_user_b on matches (user_b);

create table messages (
    id         uuid primary key,
    match_id   uuid          not null references matches (id) on delete cascade,
    sender_id  uuid          not null references users (id) on delete cascade,
    body       varchar(2000) not null,
    created_at timestamptz   not null
);
create index idx_messages_match_created on messages (match_id, created_at desc);
create index idx_messages_sender on messages (sender_id);
