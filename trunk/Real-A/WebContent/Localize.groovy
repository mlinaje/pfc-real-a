//inputs=mensaje
//outputs=mensaje
import com.webratio.rtx.RTXConstants
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* Localize.groovy ************************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);

/* OBTIENE EL MENSAJE INTERNACIONALIZADO *********************************************/
if(null != mensaje){
	try{
		String lenguaje = sessionContext.get(RTXConstants.LANGUAGE_CTX_PARAM_KEY);
		String pais = sessionContext.get(RTXConstants.COUNTRY_CTX_PARAM_KEY);
		mensaje = ResourceBundle.getBundle("ApplicationResources", new Locale(lenguaje,pais)).getString(mensaje);
	}catch(e){
   	mensaje = "???" + mensaje + "???";
	}
  
	if (log.isInfoEnabled()) log.info("SALIDA:");
	if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
 	if (log.isInfoEnabled()) log.info("* FIN Localize.groovy ********************************************");
	if (log.isInfoEnabled()) log.info("******************************************************************");

	return mensaje;
} else {
  	if (log.isInfoEnabled()) log.info("* FIN Localize.groovy ********************************************");
	if (log.isInfoEnabled()) log.info("******************************************************************");
	return "";
}

