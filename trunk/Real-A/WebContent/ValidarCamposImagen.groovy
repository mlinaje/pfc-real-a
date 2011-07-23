//inputs=anotacion|fecha|imagen|nombreImagen|sonido|nombreSonido|idObjeto|userid
//outputs=message|anotacion|fecha|imagen|nombreImagen|sonido|nombreSonido|idObjeto|userid
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());
 
def errors = false
def mensaje = "";

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidadCamposImagen.groovy *************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("anotacion:" + anotacion);
if (log.isInfoEnabled()) log.info("fecha:" + fecha);
if (log.isInfoEnabled()) log.info("imagen:" + imagen);
if (log.isInfoEnabled()) log.info("nombreImagen:" + nombreImagen);
if (log.isInfoEnabled()) log.info("sonido:" + sonido);
if (log.isInfoEnabled()) log.info("nombreSonido:" + nombreSonido);
if (log.isInfoEnabled()) log.info("idObjeto:" + idObjeto);
if (log.isInfoEnabled()) log.info("userid:" + userid);

//Comprobamos que el usuario no esté vacio
if(null == nombreImagen || "" == nombreImagen){
	errors = true;
	mensaje = "error.primero.obtener.imagen";
}

if(errors){
  if (log.isInfoEnabled()) log.info("* ERROR EN ValidadCamposImagen.groovy ****************************");
  if (log.isInfoEnabled()) log.info("******************************************************************");
	return ["resultCode":"error", "message":mensaje, "anotacion":anotacion, "fecha":fecha, "imagen":imagen, "nombreImagen":nombreImagen, "sonido":sonido, "nombreSonido":nombreSonido, "idObjeto":idObjeto, "userid":userid]
}
if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
if (log.isInfoEnabled()) log.info("anotacion:" + anotacion);
if (log.isInfoEnabled()) log.info("fecha:" + fecha);
if (log.isInfoEnabled()) log.info("imagen:" + imagen);
if (log.isInfoEnabled()) log.info("nombreImagen:" + nombreImagen);
if (log.isInfoEnabled()) log.info("nombreSonido:" + nombreSonido);
if (log.isInfoEnabled()) log.info("idObjeto:" + idObjeto);
if (log.isInfoEnabled()) log.info("userid:" + userid);
if (log.isInfoEnabled()) log.info("* FIN ValidadCamposImagen.groovy *********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "message":mensaje, "anotacion":anotacion, "fecha":fecha, "imagen":imagen, "nombreImagen":nombreImagen, "sonido":sonido, "nombreSonido":nombreSonido, "idObjeto":idObjeto, "userid":userid]


