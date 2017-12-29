create sequence if not exists sensor_seq;
create table if not exists "sensor" (
    "id" bigint not null default sensor_seq.nextval primary key,
    "serial_number" varchar(100) not null
)