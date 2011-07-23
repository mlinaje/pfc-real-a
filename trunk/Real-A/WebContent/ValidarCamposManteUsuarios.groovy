//inputs=id|usuario|usuarioAnt|email|emailAnt|grupo|grupoAnt|contrasenia
//outputs=id|usuario|email|contrasenia|grupo

import com.webratio.rtx.core.DES3UnicodeCryptImplementation
import java.util.regex.Pattern
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

//Comprobamos que el usuario y la contraseña no existan ya en la BBDD
def countUsuario = 0
def countEmail = 0

def user = null;
def userOID = null;
if(null != id){
	user = BeanHelper.asString(id);
	userOID = NumberUtils.toInt(user);
}
 
def dbId = "db1"
def session = getDBSession(dbId)
def contraseniaCorrecta = true;

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidarCamposManteUsuario.groovy *******************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("usuarioAnt:" + usuarioAnt);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("emailAnt:" + emailAnt);
if (log.isInfoEnabled()) log.info("grupo:" + grupo);
if (log.isInfoEnabled()) log.info("grupoAnt:" + grupoAnt);

//Comprobamos el usuario si ha cambiado.
if(usuario != usuarioAnt){
	//Comprobamos que el usuario no esté vacio
	if(null == usuario || "" == usuario){
		errors = true;
		mensajes.add("error.codigo.usuario.obligatorio");
	}
	//Comprobamos el usuario
	def query1 = null;
	if(null != userOID){
		query1 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE username = :usuario AND oid <> :userOID")
		query1.setParameter("usuario", usuario)
		query1.setParameter("userOID", userOID)
	}else{
		query1 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE username = :usuario")
		query1.setParameter("usuario", usuario)
	}	
	countUsuario = query1.uniqueResult()
	if (countUsuario != 0) {
		errors = true;
		mensajes.add("error.nombre.usuario.existe");
	}
} else {
	usuario = null;
}

//Comprobamos el email si ha cambiado
if(email != emailAnt){
	//Comprobamos que el email no esté vacio
	if(null == email || "" == email){
		errors = true;
		mensajes.add("error.email.usuario.obligatorio");
	}
	//Comprobamos la email
	def query2 = null; 
	if(null != userOID){
		query2 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE email = :email AND oid <> :userOID")
		query2.setParameter("email", email)
		query2.setParameter("userOID", userOID)
	} else {
		query2 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE email = :email")
		query2.setParameter("email", email)
	}
	countEmail = query2.uniqueResult()
	if (countEmail != 0) {
		errors = true;
		mensajes.add("error.cuenta.correo.existe");
	}
}else{
	email = null;
}

//comprobamos la contraseña
if("" == contrasenia){
  contrasenia = null;
}

if(null == contrasenia && null == id){
	errors = true;
	mensajes.add("error.contrasenia.usuario.obligatorio");
}

//Comprobamos que el grupo no esté vacio
if(null == grupo || "" == grupo){
	errors = true;
	mensajes.add("error.codigo.grupo.obligatorio");
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
	
  if (log.isInfoEnabled()) log.info("* ERROR EN ValidarCamposManteUsuario.groovy **********************");
  if (log.isInfoEnabled()) log.info("******************************************************************");

	return ["resultCode":"error", "id":id, "usuario":usuario, "grupo":grupo, "email":email, "contrasenia":contrasenia]
}

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("grupo:" + grupo);
if (log.isInfoEnabled()) log.info("* FIN ValidarCamposManteUsuario.groovy ***************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "id":id, "usuario":usuario, "grupo":grupo, "email":email, "contrasenia":contrasenia]


