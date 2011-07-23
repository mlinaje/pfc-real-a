import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpControlMensaje
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* BorradoControlMensaje.groovy ***********************************");

def tmpEntityId = "ent8";
VirtualEntityTable tmpTable = new VirtualEntityTable(tmpEntityId, sessionContext, service)

//Borrado de la tabla temporal
tmpTable.delete(tmpTable.getRows());

if (log.isInfoEnabled()) log.info("* FIN BorradoControlMensaje.groovy *******************************");
if (log.isInfoEnabled()) log.info("******************************************************************");
