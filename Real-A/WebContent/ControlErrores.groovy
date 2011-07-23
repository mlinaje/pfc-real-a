//inputs=esError|mensajeError
import java.sql.Timestamp
import com.webratio.rtx.RTXConstants
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlError
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ControlErrores.groovy ******************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("esError:" + esError);
if (log.isInfoEnabled()) log.info("mensajeError:" + mensajeError);
                                                               
/* OBTIENE EL MENSAJE INTERNACIONALIZADO *********************************************/
String mensaje = "";
try{
	String lenguaje = sessionContext.get(RTXConstants.LANGUAGE_CTX_PARAM_KEY);
	String pais = sessionContext.get(RTXConstants.COUNTRY_CTX_PARAM_KEY);
	mensaje = ResourceBundle.getBundle("ApplicationResources", new Locale(lenguaje,pais)).getString(mensajeError);
}catch(e){
    mensaje = "???" + mensajeError + "???";
}
/*************************************************************************************/
Boolean hayError = esError;

if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);

def tmpEntityId = "ent8";
VirtualEntityTable tmpTable = new VirtualEntityTable(tmpEntityId, sessionContext, service)

//Si hay errores, lo guarda en la tabla temporal
if(hayError){
	//Genera el registro con el mensaje de error
	TmpControlError tmpControlError = new TmpControlError();
	tmpControlError.error = mensaje;
	tmpControlError.timeStamp = new Timestamp(System.currentTimeMillis());
	tmpTable.save(tmpControlError);
}


if (log.isInfoEnabled()) log.info("* FIN ControlErrores.groovy **************************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success"]

