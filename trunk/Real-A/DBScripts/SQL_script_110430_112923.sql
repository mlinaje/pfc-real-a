-- Idioma  [ent10]
alter table "public"."objeto"  ADD COLUMN  "flag"  varchar(255);


-- Descripciones_Idiomas  [rel11]
alter table "public"."descripciones"  ADD COLUMN  "objeto_oid_2"  int4;
alter table "public"."descripciones"   add constraint fk_objeto_3 foreign key ("objeto_oid_2") references "public"."objeto" ("oid");


-- Imagen_Objeto  [rel12]
alter table "public"."imagen"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."imagen"   add constraint fk_objeto_4 foreign key ("objeto_oid") references "public"."objeto" ("oid");


