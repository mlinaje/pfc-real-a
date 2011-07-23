//inputs=id|descripcion|Language|Country|flag
//outputs=id|descripcion|Language|Country|flag

import com.webratio.rtx.RTXConstants
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlMensaje
import java.sql.Timestamp
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

def errors = false
Collection mensajes = new ArrayList();

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidadCamposIdioma.groovy *************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("descripcion:" + descripcion);
if (log.isInfoEnabled()) log.info("Language:" + Language);
if (log.isInfoEnabled()) log.info("Country:" + Country);
if (log.isInfoEnabled()) log.info("flag:" + flag);

if(null == descripcion || "" == descripcion){
	errors = true;
	mensajes.add("error.descripcion.lenguaje.obligatorio");
}
if(null == Language || "" == Language){
	errors = true;
	mensajes.add("error.Language.lenguaje.obligatorio");
}
if(null == Country || "" == Country){
	errors = true;
	mensajes.add("error.Country.lenguaje.obligatorio");
}
if(null == flag || "" == flag){
	errors = true;
	mensajes.add("error.flag.lenguaje.obligatorio");
}

if(errors){
	def tmpEntityId = "ent8";
	VirtualEntityTable tmpTable = new VirtualEntityTable(tmpEntityId, sessionContext, service)
		
	//Borrado de la tabla temporal
	tmpTable.delete(tmpTable.getRows());

	for (mensajeError in mensajes) {
		//Obtiene el mensaje internacionalizado
		String mensaje = "";
		try{
        String lenguaje = sessionContext.get(RTXConstants.LANGUAGE_CTX_PARAM_KEY);
        String pais = sessionContext.get(RTXConstants.COUNTRY_CTX_PARAM_KEY);
		    mensaje = ResourceBundle.getBundle("ApplicationResources", new Locale(lenguaje,pais)).getString(mensajeError);
		}catch(e){
		    mensaje = "???" + mensajeError + "???";
		}
		
		//Genera el registro con el mensaje de error
		TmpControlMensaje TmpControlMensaje = new TmpControlMensaje();
		TmpControlMensaje.mensaje = mensaje;
		TmpControlMensaje.timeStamp = new Timestamp(System.currentTimeMillis());
		tmpTable.save(TmpControlMensaje);
	}
	
  if (log.isInfoEnabled()) log.info("* ERROR EN ValidadCamposIdioma.groovy ****************************");
  if (log.isInfoEnabled()) log.info("******************************************************************");
	
	return ["resultCode":"error", "id":id, "descripcion":descripcion, "Language":Language, "Country":Country, "flag":flag]

}

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("descripcion:" + descripcion);
if (log.isInfoEnabled()) log.info("Language:" + Language);
if (log.isInfoEnabled()) log.info("Country:" + Country);
if (log.isInfoEnabled()) log.info("flag:" + flag);
if (log.isInfoEnabled()) log.info("* FIN ValidadCamposIdioma.groovy *********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "id":id, "descripcion":descripcion, "Language":Language, "Country":Country, "flag":flag]
