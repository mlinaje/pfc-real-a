CREATE TYPE PuntoClave AS (
  descripcion_punto text
);

-- Table: puntoscaracteristicos

-- DROP TABLE puntoscaracteristicos;

CREATE TABLE puntoscaracteristicos
(
  oid serial NOT NULL,
  plantilla_oid integer,
  x double precision,
  y double precision,
  escala integer,
  orientacion double precision,
  laplacian integer,
  hessian double precision,
  descriptores double precision[],
  CONSTRAINT puntoscaracteristicos_pkey PRIMARY KEY (oid),
  CONSTRAINT fk_plantilla FOREIGN KEY (plantilla_oid)
      REFERENCES plantilla (oid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE puntoscaracteristicos OWNER TO postgres;


----------------------------------------------------------------------------------------------------------------------
-- Extrae los puntos caracteristicos de una imagen -------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION extraer_surf(cstring) RETURNS SETOF PuntoClave
   AS 'obtenersurf', 'c_extraer_surf'
   LANGUAGE C STRICT;


----------------------------------------------------------------------------------------------------------------------
-- Obtiene la imagen con la comparación de las dos imagenes de entrada -----------------------------------------------
----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION obtener_imagen_coin(cstring,cstring,cstring) RETURNS INTEGER
   AS 'obtenersurf', 'c_obtener_imagen_coincidencias'
   LANGUAGE C STRICT;

----------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------
--CREATE OR REPLACE FUNCTION contar_coincidencias(cstring, cstring) RETURNS INTEGER
--   AS 'obtenersurf', 'c_contar_coincidencias'
--   LANGUAGE C STRICT;

----------------------------------------------------------------------------------------------------------------------
-- Obtiene la distancia cuadrada de dos vectores ---------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION distancia_cuadrada(DOUBLE PRECISION[], DOUBLE PRECISION[]) RETURNS DOUBLE PRECISION
AS 'obtenersurf', 'c_distancia_cuadrada'
LANGUAGE C STRICT;

----------------------------------------------------------------------------------------------------------------------
-- Trigger que genera los puntos caracteristicos al insertar una imagen ----------------------------------------------
----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION procesar_puntos_caracteristicos() RETURNS TRIGGER AS $procesar_puntos_caracteristicos$
    DECLARE
        puntocaracteristico PuntoClave;
	punto_x DOUBLE PRECISION;
	punto_y DOUBLE PRECISION;
	punto_escala INTEGER;
	punto_orientacion DOUBLE PRECISION;
	punto_laplacian INTEGER;
	punto_hessian DOUBLE PRECISION;
	punto_descriptores DOUBLE PRECISION[128];
	punto TEXT;
	rutaFichero CHARACTER VARYING(255);       
    BEGIN

        IF (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') THEN
	        DELETE FROM puntoscaracteristicos WHERE plantilla_oid  = OLD.oid;
        END IF;

        IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            rutaFichero = NEW.ruta || NEW.imagen;
	    FOR puntocaracteristico IN SELECT extraer_surf(textout(rutaFichero)) LOOP
		punto = replace(puntocaracteristico.descripcion_punto,'(','');
		punto = replace(punto,')','');		
		punto = replace(punto,'"','');
				
		punto_x = split_part(punto,':',1);
		punto_y = split_part(punto,':',2);
		punto_escala = split_part(punto,':',3);
		punto_orientacion = split_part(punto,':',4);
		punto_laplacian = split_part(punto,':',5);
		punto_hessian = split_part(punto,':',6);
		punto_descriptores = split_part(punto,':',7);
		INSERT INTO puntoscaracteristicos(plantilla_oid, x, y, escala, orientacion, laplacian, hessian, descriptores)
		       VALUES(NEW.oid, punto_x, punto_y, punto_escala, punto_orientacion, punto_laplacian, punto_hessian, punto_descriptores);
	    END LOOP;
	END IF;
          
        RETURN OLD;
    END;
$procesar_puntos_caracteristicos$ LANGUAGE plpgsql;

----------------------------------------------------------------------------------------------------------------------------
-- Obtiene el numero de coincidencias entre una imagen en BBDD y una imagen nueva ------------------------------------------ 
----------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION numero_de_coincidencias(oid_imagen INTEGER, ruta_imagen CHARACTER VARYING(255)) RETURNS INTEGER AS $$
    DECLARE
	mejor_distancia DOUBLE PRECISION;
	segunda_mejor DOUBLE PRECISION;
	distancia DOUBLE PRECISION;
	coincidencias INTEGER;
        puntocaracteristico PuntoClave;
	punto_laplacian INTEGER;
	punto_descriptores DOUBLE PRECISION[128];
	punto TEXT;
    BEGIN
        coincidencias:= 0;
        
        /* Crea la tabla temporal para almacenar los puntos caracteristicos de la imagen */
        IF NOT existe_tabla('puntoscaracteristicos_tmp') THEN
		CREATE TEMPORARY TABLE puntoscaracteristicos_tmp ON COMMIT DROP AS SELECT extraer_surf(textout(ruta_imagen));
        END IF;

        /* Obtiene el número de coincidencias */        
	FOR puntocaracteristico IN SELECT * FROM puntoscaracteristicos_tmp LOOP
	    mejor_distancia:= 9999;
  	    segunda_mejor:= 9999;

	    punto = replace(puntocaracteristico.descripcion_punto,'(','');
	    punto = replace(punto,')','');		
	    punto = replace(punto,'"','');
				
	    punto_laplacian = split_part(punto,':',5);
	    punto_descriptores = split_part(punto,':',7);
	    IF(punto_laplacian = 1) THEN
		    FOR distancia IN SELECT distancia_cuadrada(punto_descriptores, descriptores) AS dist FROM puntoscaracteristicos 
				      WHERE plantilla_oid = oid_imagen AND laplacian = punto_laplacian ORDER BY dist ASC LIMIT 2 LOOP
			IF distancia < mejor_distancia THEN 
			    segunda_mejor:= mejor_distancia;
			    mejor_distancia:= distancia;
			ELSE
			    IF distancia < segunda_mejor THEN	    
				segunda_mejor:= distancia;
			    END IF;
			END IF;
		    END LOOP;
		    
		    IF mejor_distancia < (0.6 * segunda_mejor) THEN
			coincidencias:= coincidencias + 1;
		    END IF;		
	    END IF;
	    IF(coincidencias > 0) THEN
		    IF(punto_laplacian = -1) THEN
			    FOR distancia IN SELECT distancia_cuadrada(punto_descriptores, descriptores) AS dist FROM puntoscaracteristicos 
					      WHERE plantilla_oid = oid_imagen AND laplacian = punto_laplacian ORDER BY dist ASC LIMIT 2 LOOP
				IF distancia < mejor_distancia THEN 
				    segunda_mejor:= mejor_distancia;
				    mejor_distancia:= distancia;
				ELSE
				    IF distancia < segunda_mejor THEN	    
					segunda_mejor:= distancia;
				    END IF;
				END IF;
			    END LOOP;
			    
			    IF mejor_distancia < (0.6 * segunda_mejor) THEN
				coincidencias:= coincidencias + 1;
			    END IF;		
		    END IF;
	    END IF;
	    
	END LOOP;

        RETURN coincidencias;
    END;
$$ LANGUAGE plpgsql;

-----------------------------------------------------------------------------------------------------------------------------
--CREATE OR REPLACE FUNCTION numero_de_coincidencias_2(ruta_imagen1 CHARACTER VARYING(255), ruta_imagen2 CHARACTER VARYING(255)) RETURNS INTEGER AS $$
--    BEGIN
--	RETURN contar_coincidencias(textout(ruta_imagen1),textout(ruta_imagen2));
--    END;
--$$ LANGUAGE plpgsql;

----------------------------------------------------------------------------------------------------------------------
-- Obtiene la imagen con la comparación de las dos imagenes de entrada -----------------------------------------------
----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION obtener_imagen_coincidencias(ruta_imagen1 CHARACTER VARYING(255), ruta_imagen2 CHARACTER VARYING(255), ruta_salida CHARACTER VARYING(255)) RETURNS INTEGER AS $$
    BEGIN
	RETURN obtener_imagen_coin(textout(ruta_imagen1), textout(ruta_imagen2), textout(ruta_salida));
    EXCEPTION WHEN OTHERS THEN
	RAISE NOTICE 'ERROR AL OBTENER IMAGENES.';
    END;
$$ LANGUAGE plpgsql;


-----------------------------------------------------------------------------------------------------------------------------
-- Comprueba si existe una tabla --------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION existe_tabla(nombre_tabla TEXT) RETURNS BOOLEAN AS $$
    DECLARE
	numero_tablas INTEGER;
    BEGIN
	SELECT count(tablename) INTO numero_tablas FROM pg_tables WHERE tablename = nombre_tabla;
	IF numero_tablas > 0 THEN
	    RETURN TRUE;
	END IF;	
	RETURN FALSE;
    END;
$$ LANGUAGE plpgsql;

-----------------------------------------------------------------------------------------
CREATE TRIGGER procesar_puntos_caracteristicos
  AFTER INSERT OR UPDATE ON plantilla
  FOR EACH ROW EXECUTE PROCEDURE procesar_puntos_caracteristicos();

CREATE TRIGGER procesar_puntos_caracteristicos_borrar
  BEFORE DELETE ON plantilla
  FOR EACH ROW EXECUTE PROCEDURE procesar_puntos_caracteristicos();

-----------------------------------------------------------------------------------------
--- PRUEBAS ---
-----------------------------------------------------------------------------------------
INSERT INTO plantilla(
            oid, ruta, imagen)
    VALUES (1, '/', 'PFC/imagenes/mama2.JPG');

UPDATE plantilla
   SET imagen='PFC/imagenes/mama1.JPG'
 WHERE oid = 1;    

DELETE FROM plantilla WHERE oid  = 1;

DELETE FROM puntoscaracteristicos;

SELECT p.ruta, p.imagen, numero_de_coincidencias(p.oid,'/PFC/imagenes/qimg0054.JPG') AS coincidencias 
  FROM plantilla p;  WHERE numero_de_coincidencias(p.oid,'/PFC/imagenes/qimg0054.JPG') >= 15;

SELECT o.descripcion , o.informacion, p.ruta, p.imagen, numero_de_coincidencias(p.oid,'/PFC/imagenes/qimg0054.JPG') AS coincidencias 
  FROM plantilla p 
  INNER JOIN objeto AS o ON o.oid = p.objeto_oid
--  WHERE numero_de_coincidencias(p.oid,'/PFC/imagenes/qimg0111.JPG') >= 15 
  ORDER BY coincidencias DESC LIMIT 10;

SELECT p.ruta, p.imagen, numero_de_coincidencias_2(p.ruta || p.imagen,'/PFC/imagenes/qimg0054.JPG') AS coincidencias 
  FROM plantilla p;

  
SELECT count(*) FROM puntoscaracteristicos  WHERE plantilla_oid = 4;
SELECT count(*) FROM puntoscaracteristicos  WHERE plantilla_oid = 5;
SELECT count(*) FROM puntoscaracteristicos  WHERE plantilla_oid = 10;
SELECT count(*) FROM puntoscaracteristicos  WHERE plantilla_oid = 11;



SELECT obtener_imagen_coincidencias('/PFC/imagenes/qimg0105.JPG',
				    '/PFC/imagenes/qimg0106.JPG',
				    '/PFC/imagenes/XXX.JPG');



SELECT o.descripcion , o.informacion, plantilla.ruta, plantilla.imagen, numero_de_coincidencias(plantilla.oid,'/PFC/WebRatio/WebRatio Enterprise 6.0.0Beta/Tomcat/webapps/Real-A/UploadFotos/22012011504.jpg') AS coincidencias FROM plantilla INNER JOIN objeto AS o ON o.oid = plantilla.objeto_oid ORDER BY coincidencias DESC LIMIT 1;


SELECT o.descripcion , o.informacion, plantilla.ruta, plantilla.imagen, numero_de_coincidencias(plantilla.oid,'/PFC/WebRatio/WebRatio Enterprise 6.0.0Beta/Tomcat/webapps/Real-A/upload/virtual/ImagenTemporal/1/qimg0067.JPG') AS coincidencias FROM plantilla INNER JOIN objeto AS o ON o.oid = plantilla.objeto_oid ORDER BY coincidencias DESC;


SELECT * FROM puntoscaracteristicos where laplacian  = 1;