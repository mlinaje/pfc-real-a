-- Plantilla  [ent2]
create table "public"."plantilla" (
   "oid"  int4  not null,
   "ruta"  varchar(255),
   "imagen"  varchar(255),
  primary key ("oid")
);


-- Plantilla_Tmp  [ent3]
create table "public"."plantilla_tmp" (
   "oid"  int4  not null,
   "ruta"  varchar(255),
   "imagen"  varchar(255),
  primary key ("oid")
);


-- Puntoscaracteristicos  [ent4]
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


-- Puntoscaracteristicos_Tmp  [ent5]
create table "public"."puntoscaracteristicos_tmp" (
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


-- PuntoscaracteristicostmpPlantillatmp  [rel6]
alter table "public"."puntoscaracteristicos_tmp"  ADD COLUMN  "plantilla_tmp_oid"  int4;
alter table "public"."puntoscaracteristicos_tmp"   add constraint fk_plantilla_tmp foreign key ("plantilla_tmp_oid") references "public"."plantilla_tmp" ("oid");


-- PuntoscaracteristicosPlantilla  [rel7]
alter table "public"."puntoscaracteristicos"  ADD COLUMN  "plantilla_oid"  int4;
alter table "public"."puntoscaracteristicos"   add constraint fk_plantilla foreign key ("plantilla_oid") references "public"."plantilla" ("oid");


