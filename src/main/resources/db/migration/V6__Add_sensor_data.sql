create sequence if not exists sensor_data_seq;
create table if not exists "sensor_data" (
    "id" bigint not null default sensor_data_seq.nextval primary key,
    "sensor_id" bigint not null,
    "measurable_parameter" varchar(5) not null,
    "num_value" double,
    "str_value" varchar(250),
    "geo_lat" double,
    "geo_long" double,
    "time" timestamp not null default current_timestamp,
     foreign key("sensor_id") references "sensor"("id"),
     check ("measurable_parameter" in ('LOC', 'TEMP', 'HUMD', 'NS', 'CID'))
)