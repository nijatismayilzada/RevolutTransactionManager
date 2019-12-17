create table if not exists transaction
(
    transaction_id     int auto_increment not null,
    account_id         int,
    reference          text,
    transaction_state  text,
    state_updated_at   datetime,
    transaction_type   text,
    currency           text,
    amount             decimal(15, 2),
    primary key (transaction_id)
);