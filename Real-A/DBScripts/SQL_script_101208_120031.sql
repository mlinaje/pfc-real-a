-- Plantilla  [ent2]
create table "public"."plantilla" (
   "oid"  int4  not null,
   "ruta"  varchar(255),
   "imagen"  varchar(255),
  primary key ("oid")
);


-- PlantillaTmp  [ent3]
create table "public"."plantillatmp" (
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


-- PuntoscaracteristicosTmp  [ent5]
create table "public"."puntoscaracteristicostmp" (
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
alter table "public"."puntoscaracteristicostmp"  ADD COLUMN  "plantillatmp_oid"  int4;
alter table "public"."puntoscaracteristicostmp"   add constraint fk_plantillatmp foreign key ("plantillatmp_oid") references "public"."plantillatmp" ("oid");


-- PuntoscaracteristicosPlantilla  [rel7]
alter table "public"."puntoscaracteristicos"  ADD COLUMN  "plantilla_oid"  int4;
alter table "public"."puntoscaracteristicos"   add constraint fk_plantilla foreign key ("plantilla_oid") references "public"."plantilla" ("oid");


