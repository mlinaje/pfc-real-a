//inputs=usuario|email|contrasenia|confirmarContrasenia
//outputs=message|usuario|email|contrasenia
import java.util.regex.Pattern
import java.sql.Timestamp
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import com.webratio.rtx.RTXConstants
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlMensaje
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ValidarCamposUsuario.groovy ************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("email:" + email);


def errors = false

Collection mensajes = new ArrayList();

//Comprobamos que el usuario y la contraseña no existan ya en la BBDD
def countUsuario = 0
def countEmail = 0

def dbId = "db1"
def session = getDBSession(dbId)

//Comprobamos la contraseña
def query2 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE email = :email")
query2.setParameter("email", email)
countEmail = query2.uniqueResult()
if (countEmail != 0) {
	errors = true;
	mensajes.add("error.cuenta.correo.existe");
}

//Comprobamos el usuario
def query1 = session.createSQLQuery("SELECT count(*) FROM \"user\" WHERE username = :usuario")
query1.setParameter("usuario", usuario)
countUsuario = query1.uniqueResult()
if (countUsuario != 0) {
	errors = true;
	mensajes.add("error.nombre.usuario.existe");
}

//Comprobamos que la contraseña y su confirmación sean iguales.
if(confirmarContrasenia != contrasenia){
	errors = true;
	mensajes.add("error.contrasenia.confirmacion.distintas");
}

//Comprobamos que la contraseña no esté vacia
if(null == contrasenia || "" == contrasenia){
	errors = true;
	mensajes.add("error.contrasenia.usuario.obligatorio");
}

//Comprobamos que el email sea valido
if(null != email && "" != email && !Pattern.matches(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/, email)){
	errors = true;
	mensajes.add("error.email.usuario.valido");
}

//Comprobamos que el email no esté vacio
if(null == email || "" == email){
	errors = true;
	mensajes.add("error.email.usuario.obligatorio");
}

//Comprobamos que el usuario no esté vacio
if(null == usuario || "" == usuario){
	errors = true;
	mensajes.add("error.codigo.usuario.obligatorio");
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
		
		if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
		
		//Genera el registro con el mensaje de error
		TmpControlMensaje TmpControlMensaje = new TmpControlMensaje();
		TmpControlMensaje.mensaje = mensaje;
		TmpControlMensaje.timeStamp = new Timestamp(System.currentTimeMillis());
		tmpTable.save(TmpControlMensaje);
	}
}

if(errors){
	if (log.isInfoEnabled()) log.info("* ERROR EN ValidarCamposUsuario.groovy ***************************");
	if (log.isInfoEnabled()) log.info("******************************************************************");
	return ["resultCode":"error", "usuario":usuario, "email":email, "contrasenia":contrasenia]
}

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("* FIN ValidarCamposUsuario.groovy ********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "usuario":usuario, "email":email, "contrasenia":contrasenia]


