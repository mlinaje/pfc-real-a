//inputs=id|usuario|usuarioAntiguo|email|emailAntiguo|antiguaContrasenia|contrasenia|confirmarContrasenia
//outputs=id|usuario|email|contrasenia
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
def user = BeanHelper.asString(id);
def userOID = NumberUtils.toInt(user);

def dbId = "db1"
def session = getDBSession(dbId)
def contraseniaCorrecta = true;

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidarCamposMantenimientoUsuario.groovy ***********************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("usuarioAntiguo:" + usuarioAntiguo);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("emailAntiguo:" + emailAntiguo);

//Comprobamos la antigua contraseña
def query3 = session.createSQLQuery("SELECT password FROM \"user\" WHERE oid = :userOID")
query3.setParameter("userOID", userOID)
oldContrasenia = query3.uniqueResult()
DES3UnicodeCryptImplementation encriptar = new DES3UnicodeCryptImplementation();
String contEncriptada = encriptar.encrypt(antiguaContrasenia);	
if (oldContrasenia != contEncriptada) {
	contraseniaCorrecta = false;
}

//Comprobamos el usuario si ha cambiado.
if(usuario != usuarioAntiguo){
	//Comprobamos que el usuario no esté vacio
	if(null == usuario || "" == usuario){
		errors = true;
		mensajes.add("error.codigo.usuario.obligatorio");
	}
	//Comprobamos el usuario
	def query1 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE username = :usuario AND oid <> :userOID")
	query1.setParameter("usuario", usuario)
	query1.setParameter("userOID", userOID)
	countUsuario = query1.uniqueResult()
	if (countUsuario != 0) {
		errors = true;
		mensajes.add("error.nombre.usuario.existe");
	}
} else {
	usuario = null;
}

//Comprobamos el email si ha cambiado
if(email != emailAntiguo){
	//Comprobamos que el email no esté vacio
	if(null == email || "" == email){
		errors = true;
		mensajes.add("error.email.usuario.obligatorio");
	}
	//Comprobamos la email
	def query2 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE email = :email AND oid <> :userOID")
	query2.setParameter("email", email)
	query2.setParameter("userOID", userOID)
	countEmail = query2.uniqueResult()
	if (countEmail != 0) {
		errors = true;
		mensajes.add("error.cuenta.correo.existe");
	}
	if(antiguaContrasenia == null){
		errors = true;
		mensajes.add("error.campo.contrasenia.antigua.obligatoria.email");
	} else {
		if(!contraseniaCorrecta){
			errors = true;
			mensajes.add("error.campo.contrasenia.antigua.no.coinciden");
			contraseniaCorrecta = true;
		}
	}
}else{
	email = null;
}
	
if((null != contrasenia && contrasenia != "") || (null != confirmarContrasenia && confirmarContrasenia != "")){
	//Comprobamos que la contraseña no esté vacia
	if(null == contrasenia || "" == contrasenia){
		errors = true;
		mensajes.add("error.campo.contrasenia.obligatorio");
	}
	
	//Comprobamos que la contraseña y su confirmación sean iguales.
	if(confirmarContrasenia != contrasenia){
		errors = true;
		mensajes.add("error.campo.contrasenia.confirmacion.no.coinciden");
	}
	
	if(antiguaContrasenia == null){
		errors = true;
		mensajes.add("error.campo.contrasenia.antigua.obligatoria.password");
	} else {
		if(!contraseniaCorrecta){
			errors = true;
			mensajes.add("error.campo.contrasenia.antigua.no.coinciden");
		}
	}
} else {
	contrasenia = null;
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
	
  if (log.isInfoEnabled()) log.info("* ERROR EN ValidarCamposMantenimientoUsuario.groovy **************");
  if (log.isInfoEnabled()) log.info("******************************************************************");


	return ["resultCode":"error", "id":id, "usuario":usuario, "email":email, "contrasenia":contrasenia]
}

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("id:" + id);
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("* FIN ValidarCamposMantenimientoUsuario.groovy *******************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "id":id, "usuario":usuario, "email":email, "contrasenia":contrasenia]


