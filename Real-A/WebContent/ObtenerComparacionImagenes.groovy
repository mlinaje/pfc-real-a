//inputs=rutaImagen1|nombreImagen1|rutaImagen2|nombreImagen2
//outputs=rutaSalida|nombreSalida

import com.sun.org.apache.xml.internal.serializer.ToStream
import com.webratio.rtx.core.BeanHelper
import org.apache.commons.lang.math.NumberUtils
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ObtenerComparacionImagenes.groovy ******************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("rutaImagen1:" + rutaImagen1);
if (log.isInfoEnabled()) log.info("nombreImagen1:" + nombreImagen1);
if (log.isInfoEnabled()) log.info("rutaImagen2:" + rutaImagen2);
if (log.isInfoEnabled()) log.info("nombreImagen2:" + nombreImagen2);


String rutaSalida   = rutaImagen2;
String nombreSalida = nombreImagen2;
int dotPos = nombreSalida.lastIndexOf(".");
String extension = nombreSalida.substring(dotPos);
Calendar fecha = new GregorianCalendar();
nombreSalida = nombreSalida.replace(extension, fecha.getTimeInMillis().toString() + extension);

String pathCompleto1 = rutaImagen1 + nombreImagen1;
String pathCompleto2 = rutaImagen2 + nombreImagen2;
String pathSalida    = rutaSalida + nombreSalida;

pathCompleto1 = pathCompleto1.replace("{", "");
pathCompleto1 = pathCompleto1.replace("}", "");
pathCompleto2 = pathCompleto2.replace("{", "");
pathCompleto2 = pathCompleto2.replace("}", "");

def dbId = "db1";
def session = getDBSession(dbId);

//Realiza la busqueda de las imagenes
def select = "SELECT obtener_imagen_coincidencias(:pathCompleto1, :pathCompleto2, :pathSalida)";
  			 
def querySeleccion = session.createSQLQuery(select);
querySeleccion.setParameter("pathCompleto1", pathCompleto1);
querySeleccion.setParameter("pathCompleto2", pathCompleto2);
querySeleccion.setParameter("pathSalida", pathSalida);
def coincidencias = querySeleccion.uniqueResult();

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("rutaSalida:" + rutaSalida);
if (log.isInfoEnabled()) log.info("nombreSalida:" + nombreSalida);
if (log.isInfoEnabled()) log.info("* FIN ObtenerComparacionImagenes.groovy **************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "rutaSalida":rutaSalida, "nombreSalida":nombreSalida]
