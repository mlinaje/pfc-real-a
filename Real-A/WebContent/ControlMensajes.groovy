//inputs=mensaje
import java.sql.Timestamp
import com.webratio.rtx.RTXConstants
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlMensaje
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());
if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ControlMensajes.groovy ******************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);

if(null != mensaje && mensaje != ""){
	/* OBTIENE EL MENSAJE INTERNACIONALIZADO *********************************************/
	try{
		String lenguaje = sessionContext.get(RTXConstants.LANGUAGE_CTX_PARAM_KEY);
		String pais = sessionContext.get(RTXConstants.COUNTRY_CTX_PARAM_KEY);
		mensaje = ResourceBundle.getBundle("ApplicationResources", new Locale(lenguaje,pais)).getString(mensaje);
	}catch(e){
	    mensaje = "???" + mensaje + "???";
	}
	/*************************************************************************************/

  if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);

	def tmpEntityId = "ent8";
	VirtualEntityTable tmpTable = new VirtualEntityTable(tmpEntityId, sessionContext, service)
	
	//Borrado de la tabla temporal
	tmpTable.delete(tmpTable.getRows());
	
	//Genera el registro con el mensaje de error
	TmpControlMensaje tmpControlMensaje = new TmpControlMensaje();
	tmpControlMensaje.mensaje = mensaje;
	tmpControlMensaje.timeStamp = new Timestamp(System.currentTimeMillis());
	tmpTable.save(tmpControlMensaje);
}

if (log.isInfoEnabled()) log.info("* FIN ControlMensajes.groovy **************************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success"]

