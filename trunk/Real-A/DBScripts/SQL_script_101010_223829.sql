-- FeaturePoints  [ent2]
create table "public"."featurepoints" (
   "oid"  int4  not null,
   "x"  float8,
   "y"  float8,
   "strength"  float8,
   "trace"  float8,
   "sign"  bool,
   "scale"  float8,
   "orientation"  float8,
   "descriptor"  float8,
  primary key ("oid")
);


