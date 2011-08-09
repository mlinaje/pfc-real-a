-- User  [User]
create table "public"."user" (
   "oid"  int4  not null,
   "username"  varchar(255),
   "password"  varchar(255),
   "email"  varchar(255),
  primary key ("oid")
);


-- Group  [Group]
create table "public"."group" (
   "oid"  int4  not null,
   "groupname"  varchar(255),
  primary key ("oid")
);


-- Module  [Module]
create table "public"."module" (
   "oid"  int4  not null,
   "moduleid"  varchar(255),
   "modulename"  varchar(255),
  primary key ("oid")
);


-- Imagen  [ent1]
create table "public"."imagen" (
   "oid"  int4  not null,
   "imagen"  varchar(255),
   "anotacion"  varchar(255),
   "hora"  timestamp,
   "sonido"  varchar(255),
  primary key ("oid")
);


-- Plantilla  [ent2]
create table "public"."plantilla" (
   "oid"  int4  not null,
   "ruta"  varchar(255),
   "imagen"  varchar(255),
  primary key ("oid")
);


-- Puntoscaracteristicos  [ent6]
create table "public"."puntoscaracteristicos" (
   "oid"  int4  not null,
   "x"  float8,
   "y"  float8,
   "escala"  int4,
   "orientacion"  float8,
   "laplacian"  int4,
   "hessian"  float8,
   "descriptores"  varchar(255),
  primary key ("oid")
);


-- Objeto  [ent4]
create table "public"."objeto" (
   "oid"  int4  not null,
   "descripcion"  varchar(255),
  primary key ("oid")
);


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


-- User_Group  [rel1]
create table "public"."user_group" (
   "user_oid"  int4 not null,
   "group_oid"  int4 not null,
  primary key ("user_oid", "group_oid")
);
alter table "public"."user_group"   add constraint fk_user_group_user foreign key ("user_oid") references "public"."user" ("oid");
alter table "public"."user_group"   add constraint fk_user_group_group foreign key ("group_oid") references "public"."group" ("oid");


-- User_DefaultGroup  [rel2]
alter table "public"."user"  ADD COLUMN  "group_oid"  int4;
alter table "public"."user"   add constraint fk_group foreign key ("group_oid") references "public"."group" ("oid");


-- Group_DefaultModule  [rel3]
alter table "public"."group"  ADD COLUMN  "module_oid"  int4;
alter table "public"."group"   add constraint fk_module foreign key ("module_oid") references "public"."module" ("oid");


-- Group_Module  [rel4]
create table "public"."group_module" (
   "group_oid"  int4 not null,
   "module_oid"  int4 not null,
  primary key ("group_oid", "module_oid")
);
alter table "public"."group_module"   add constraint fk_group_module_group foreign key ("group_oid") references "public"."group" ("oid");
alter table "public"."group_module"   add constraint fk_group_module_module foreign key ("module_oid") references "public"."module" ("oid");


-- User_Imagen  [rel5]
alter table "public"."imagen"  ADD COLUMN  "user_oid"  int4;
alter table "public"."imagen"   add constraint fk_user foreign key ("user_oid") references "public"."user" ("oid");


-- PuntoscaracteristicosPlantilla  [rel9]
alter table "public"."puntoscaracteristicos"  ADD COLUMN  "plantilla_oid"  int4;
alter table "public"."puntoscaracteristicos"   add constraint fk_plantilla foreign key ("plantilla_oid") references "public"."plantilla" ("oid");


-- Objeto_Plantilla  [rel6]
alter table "public"."plantilla"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."plantilla"   add constraint fk_objeto foreign key ("objeto_oid") references "public"."objeto" ("oid");


-- Objeto_Descripciones  [rel7]
alter table "public"."descripciones"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."descripciones"   add constraint fk_objeto_2 foreign key ("objeto_oid") references "public"."objeto" ("oid");


-- Descripciones_Idiomas  [rel11]
alter table "public"."descripciones"  ADD COLUMN  "idioma_oid"  int4;
alter table "public"."descripciones"   add constraint fk_idioma foreign key ("idioma_oid") references "public"."idioma" ("oid");


-- Imagen_Objeto  [rel12]
alter table "public"."imagen"  ADD COLUMN  "objeto_oid"  int4;
alter table "public"."imagen"   add constraint fk_objeto_3 foreign key ("objeto_oid") references "public"."objeto" ("oid");


