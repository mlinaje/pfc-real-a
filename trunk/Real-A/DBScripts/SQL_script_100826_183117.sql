-- Imagen  [ent1]
alter table "public"."imagen"  ADD COLUMN  "longitud"  numeric(19, 2);
alter table "public"."imagen"  ADD COLUMN  "latitud"  int4;
alter table "public"."imagen"  ADD COLUMN  "fecha"  time;


