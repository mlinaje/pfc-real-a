
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