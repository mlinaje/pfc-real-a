-- Descripciones  [ent9]
create table "public"."descripciones" (
   "oid"  int4  not null,
   "descripcion"  varchar(255),
   "informacion"  text,
  primary key ("oid")
);


-- Idioma  [ent10]
create table "public"."idioma" (
   "oid"  int4  not null,
   "descripcion"  varchar(255),
   "language"  varchar(255),
   "country"  varchar(255),
   "flag"  varchar(255),
  primary key ("oid")
);


-- Objeto_Descripciones  [rel7]
alter table "public"."descripciones"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."descripciones"   add constraint fk_objeto_2 foreign key ("objeto_oid") references "public"."objeto" ("oid");


-- Descripciones_Idiomas  [rel11]
alter table "public"."descripciones"  ADD COLUMN  "idioma_oid"  int4;
alter table "public"."descripciones"   add constraint fk_idioma foreign key ("idioma_oid") references "public"."idioma" ("oid");


-- Imagen_Objeto  [rel12]
alter table "public"."imagen"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."imagen"   add constraint fk_objeto_3 foreign key ("objeto_oid") references "public"."objeto" ("oid");


