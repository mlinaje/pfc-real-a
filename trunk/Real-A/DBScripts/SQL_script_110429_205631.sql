-- Descripciones  [ent9]
create table "public"."descripciones" (
   "oid"  int4  not null,
   "language"  varchar(255),
   "country"  varchar(255),
   "descripcion"  varchar(255),
   "informacion"  text,
  primary key ("oid")
);


-- Objeto_Descripciones  [rel7]
alter table "public"."descripciones"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."descripciones"   add constraint fk_objeto_2 foreign key ("objeto_oid") references "public"."objeto" ("oid");


