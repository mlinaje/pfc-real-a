//inputs=nombreUsuario
//outputs=message
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());
if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* GenerarMensajeEntrada.groovy ***********************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("nombreUsuario:" + nombreUsuario);

def mensaje = "";

mensaje = nombreUsuario + " bienvenido a Real-A.";

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("mensaje:" + mensaje);
if (log.isInfoEnabled()) log.info("* FIN GenerarMensajeEntrada.groovy *******************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "message":mensaje]


