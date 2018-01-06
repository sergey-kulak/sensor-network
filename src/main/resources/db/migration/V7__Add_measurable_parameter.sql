insert into "sensor_measurable_parameter" ("sensor_id", "measurable_parameter")
select "id", 'TEMP' from "sensor"
union
select "id", 'HUMD' from "sensor"
union
select "id", 'NS' from "sensor"