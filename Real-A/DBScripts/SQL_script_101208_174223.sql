-- Objeto  [ent4]
create table "public"."objeto" (
   "oid"  int4  not null,
   "informacion"  text,
   "descripcion"  varchar(255),
  primary key ("oid")
);


-- Objeto_Plantilla  [rel6]
alter table "public"."plantilla"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."plantilla"   add constraint fk_objeto foreign key ("objeto_oid") references "public"."objeto" ("oid");


