//outputs=pathCompleto
import org.apache.commons.io.FilenameUtils
import com.webratio.units.content.rtx.blob.UploadedBLOBData
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ObtenerPathCompleto.groovy *************************************");

String pathCompleto = "/" + FilenameUtils.separatorsToUnix(FilenameUtils.getPath(rtx.getUploadDirectory()));

if (log.isInfoEnabled()) log.info("* FIN ObtenerPathCompleto.groovy *********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "pathCompleto":pathCompleto]


