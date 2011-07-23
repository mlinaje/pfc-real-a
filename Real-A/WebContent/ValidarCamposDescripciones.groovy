//inputs=id|descripcion|informacion|idioma|objeto
//outputs=id|descripcion|informacion|idioma|objeto

import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import com.webratio.rtx.core.BeanHelper
import org.apache.commons.lang.math.NumberUtils
import com.webratio.rtx.RTXConstants
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlMensaje
import java.sql.Timestamp
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

def errors = false
Collection mensajes = new ArrayList();

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidadCamposDescripciones.groovy ******************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("descripcion:" + descripcion);
if (log.isInfoEnabled()) log.info("informacion:" + informacion);
if (log.isInfoEnabled()) log.info("idioma:" + idioma);

def descri = BeanHelper.asString(id);
def descriOid = NumberUtils.toInt(descri);

def lengua = BeanHelper.asString(idioma);
def lenguaOid = NumberUtils.toInt(lengua);

def objet = BeanHelper.asString(objeto);
def objetOid = NumberUtils.toInt(objet);

if (log.isInfoEnabled()) log.info("descriOid:" + descriOid);
if (log.isInfoEnabled()) log.info("lenguaOid:" + lenguaOid);
if (log.isInfoEnabled()) log.info("objetOid:" + objetOid);

if(null == descripcion || "" == descripcion){
	errors = true;
	mensajes.add("error.descripcion.descripcion.obligatorio");
}

if(null == informacion || "" == informacion){
	errors = true;
	mensajes.add("error.informacion.descripcion.obligatorio");
}

if(null == idioma || "" == idioma){
	errors = true;
	mensajes.add("error.idioma.descripcion.obligatorio");
} else {
	//Se comprueba que no exista ya una descripción para ese idioma
	def dbId = "db1";
	def session = getDBSession(dbId);
	def query1 = null;
	if(null == descriOid){
  		query1 = session.createSQLQuery("SELECT COUNT(*) FROM \"objeto\" as o INNER JOIN \"descripciones\" as d ON d.objeto_oid = o.oid WHERE o.oid = :objetOid AND d.idioma_oid = :lenguaOid");
    	query1.setParameter("lenguaOid", lenguaOid);
    	query1.setParameter("objetOid", objetOid);
  	} else {
    	query1 = session.createSQLQuery("SELECT COUNT(*) FROM \"objeto\" as o INNER JOIN \"descripciones\" as d ON d.objeto_oid = o.oid WHERE o.oid = :objetOid AND d.oid <> :descriOid AND d.idioma_oid = :lenguaOid");
    	query1.setParameter("descriOid", descriOid);
    	query1.setParameter("lenguaOid", lenguaOid);
    	query1.setParameter("objetOid", objetOid); 
	}
	
	countDescri = query1.uniqueResult()
	if (countDescri != 0) {
		errors = true;
		mensajes.add("error.idioma.descripcion.existe");
	}
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
	
  if (log.isInfoEnabled()) log.info("* ERROR EN ValidadCamposDescripciones.groovy ****************************");
  if (log.isInfoEnabled()) log.info("******************************************************************");
	
  return ["resultCode":"error", "id":id, "descripcion":descripcion, "informacion":informacion, "idioma":idioma, "objeto":objeto]

}

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("descripcion:" + descripcion);
if (log.isInfoEnabled()) log.info("informacion:" + informacion);
if (log.isInfoEnabled()) log.info("idioma:" + idioma);
if (log.isInfoEnabled()) log.info("* FIN ValidadCamposDescripciones.groovy *********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "id":id, "descripcion":descripcion, "informacion":informacion, "idioma":idioma, "objeto":objeto]
