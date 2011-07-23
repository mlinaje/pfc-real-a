-- User  [User]
create table "public"."user_2" (
   "oid"  int4  not null,
   "username"  varchar(255),
   "password"  varchar(255),
   "email"  varchar(255),
  primary key ("oid")
);


-- Group  [Group]
create table "public"."group_2" (
   "oid"  int4  not null,
   "groupname"  varchar(255),
  primary key ("oid")
);


-- Module  [Module]
create table "public"."module_2" (
   "oid"  int4  not null,
   "moduleid"  varchar(255),
   "modulename"  varchar(255),
  primary key ("oid")
);


-- Imagen  [ent1]
create table "public"."imagen_2" (
   "oid"  int4  not null,
   "imagen"  varchar(255),
   "informacion"  varchar(255),
   "anotacion"  varchar(255),
   "longitud"  float8,
   "latitud"  float8,
   "fecha"  time,
  primary key ("oid")
);


-- User_Group  [rel1]
create table "public"."user_group_2" (
   "user_2_oid"  int4 not null,
   "group_2_oid"  int4 not null,
  primary key ("user_2_oid", "group_2_oid")
);
alter table "public"."user_group_2"   add constraint fk_user_group_2_user_2 foreign key ("user_2_oid") references "public"."user_2" ("oid");
alter table "public"."user_group_2"   add constraint fk_user_group_2_group_2 foreign key ("group_2_oid") references "public"."group_2" ("oid");


-- User_DefaultGroup  [rel2]
alter table "public"."user_2"  ADD COLUMN  "group_2_oid"  int4;
alter table "public"."user_2"   add constraint fk_group_2 foreign key ("group_2_oid") references "public"."group_2" ("oid");


-- Group_DefaultModule  [rel3]
alter table "public"."group_2"  ADD COLUMN  "module_2_oid"  int4;
alter table "public"."group_2"   add constraint fk_module_2 foreign key ("module_2_oid") references "public"."module_2" ("oid");


-- Group_Module  [rel4]
create table "public"."group_module_2" (
   "group_2_oid"  int4 not null,
   "module_2_oid"  int4 not null,
  primary key ("group_2_oid", "module_2_oid")
);
alter table "public"."group_module_2"   add constraint fk_group_module_2_group_2 foreign key ("group_2_oid") references "public"."group_2" ("oid");
alter table "public"."group_module_2"   add constraint fk_group_module_2_module_2 foreign key ("module_2_oid") references "public"."module_2" ("oid");


-- User_Imagen  [rel5]
alter table "public"."imagen_2"  ADD COLUMN  "user_2_oid"  int4;
alter table "public"."imagen_2"   add constraint fk_user_2 foreign key ("user_2_oid") references "public"."user_2" ("oid");


