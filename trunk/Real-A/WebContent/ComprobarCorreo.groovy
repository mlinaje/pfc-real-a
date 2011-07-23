//inputs=email|contrasenia
//outputs=message|usuario|email|contrasenia
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());
if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ComprobarCrorreo.groovy ****************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("contrasenia:" + contrasenia);

def errors = false
def dbId = "db1"
def session = getDBSession(dbId)
def mensaje = "";
def usuario = null;

//Comprobamos la contraseña
def query2 = session.createSQLQuery("SELECT o.userName FROM \"user\" AS o WHERE email = :email")
query2.setParameter("email", email)
usuario = query2.uniqueResult()

if (null == usuario) {
	errors = true;
	mensaje = "error.cuenta.correo.no.existe";
}

if(errors){
  if (log.isInfoEnabled()) log.info("SALIDA:");
  if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
  if (log.isInfoEnabled()) log.info("usuario:" + usuario);
  if (log.isInfoEnabled()) log.info("email:" + email);
  if (log.isInfoEnabled()) log.info("contrasenia:" + contrasenia);
  if (log.isInfoEnabled()) log.info("* TERMINA CON ERROR ComprobarCrorreo.groovy **********************");
  if (log.isInfoEnabled()) log.info("******************************************************************");
	
  return ["resultCode":"error", "message":mensaje, "usuario":usuario, "email":email, "contrasenia":contrasenia]
}
if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
if (log.isInfoEnabled()) log.info("usuario:" + usuario);
if (log.isInfoEnabled()) log.info("email:" + email);
if (log.isInfoEnabled()) log.info("contrasenia:" + contrasenia);
if (log.isInfoEnabled()) log.info("* FIN ComprobarCrorreo.groovy ************************************");
if (log.isInfoEnabled()) log.info("******************************************************************");
return ["resultCode":"success", "message":mensaje, "usuario":usuario, "email":email, "contrasenia":contrasenia]


