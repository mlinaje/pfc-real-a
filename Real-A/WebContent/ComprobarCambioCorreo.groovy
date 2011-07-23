//inputs=email|emailAntiguo
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ComprobarCambioCrorreo.groovy **********************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("emailAntiguo:" + emailAntiguo);

if(email == emailAntiguo){
	if (log.isInfoEnabled()) log.info("TERMINA CORRECTAMENTE");
	if (log.isInfoEnabled()) log.info("* FIN ComprobarCambioCrorreo.groovy ******************************");
	return ["resultCode":"success"]
} else {
	if (log.isInfoEnabled()) log.info("SALE CON ERROR");
	if (log.isInfoEnabled()) log.info("* FIN ComprobarCambioCrorreo.groovy ******************************");
	return ["resultCode":"error"]
}