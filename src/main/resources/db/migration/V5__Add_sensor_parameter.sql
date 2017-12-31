create table if not exists "sensor_measurable_parameter" (
    "sensor_id" bigint not null,
    "measurable_parameter" varchar(5) not null,
     foreign key("sensor_id") references "sensor"("id"),
     check ("measurable_parameter" in ('LOC', 'TEMP', 'HUMD', 'NS'))
)