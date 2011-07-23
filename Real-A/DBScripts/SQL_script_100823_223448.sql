-- Imagenes  [ent1]
create table "public"."imagenes" (
   "oid"  int4  not null,
   "imagen"  varchar(255),
   "informacion"  varchar(255),
   "anotacion"  varchar(255),
  primary key ("oid")
);


-- User_Imagenes  [rel5]
alter table "public"."user"  ADD COLUMN  "imagenes_oid"  int4;
alter table "public"."user"   add constraint fk_imagenes foreign key ("imagenes_oid") references "public"."imagenes" ("oid");


