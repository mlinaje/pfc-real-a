//inputs=imagen|nombreImagen
//outputs=fecha|imagen|nombreImagen|idObjeto
import java.sql.Timestamp
import com.webratio.rtx.core.BeanHelper
import org.apache.commons.lang.math.NumberUtils
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpBusqueda
import com.webratio.rtx.RTXBLOBData;
import com.webratio.rtx.blob.BLOBData;
import org.apache.commons.io.FilenameUtils
import com.webratio.units.content.rtx.blob.UploadedBLOBData
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* BuscarResultadoImagen.groovy ***********************************");

String rutaEntrada = "/" + FilenameUtils.separatorsToUnix(FilenameUtils.getPath(rtx.getUploadDirectory()));

String pathCompleto = rutaEntrada + imagen;
Timestamp fecha = new Timestamp(System.currentTimeMillis());

if (log.isInfoEnabled()) log.info("RutaEntrada:" + rutaEntrada);
if (log.isInfoEnabled()) log.info("PathCompleto:" + pathCompleto);
if (log.isInfoEnabled()) log.info("Imagen:" + imagen);
if (log.isInfoEnabled()) log.info("NombreImagen:" + nombreImagen);

pathCompleto = pathCompleto.replace("{", "");
pathCompleto = pathCompleto.replace("}", "");

if (log.isInfoEnabled()) log.info("PathCompleto:" + pathCompleto);

def dbId = "db1";
def session = getDBSession(dbId);

//Realiza la busqueda de las imagenes
def select = "SELECT o.oid, " +
			 "\"plantilla\".ruta, \"plantilla\".imagen, " +
  			 "numero_de_coincidencias(\"plantilla\".oid,:pathCompleto) AS coincidencias " + 
  			 "FROM \"plantilla\" " +
  			 "INNER JOIN \"objeto\" AS o ON o.oid = \"plantilla\".objeto_oid " + 
  			 "ORDER BY coincidencias DESC LIMIT 1";
  			 
if (log.isInfoEnabled()) log.info("Select:" + select);

try{
	def querySeleccion = session.createSQLQuery(select);
	querySeleccion.setParameter("pathCompleto", pathCompleto);
	def result = querySeleccion.list();
	//Genera la tabla temporal con los registros de la query
	for (Iterator iter = result.iterator(); iter.hasNext();) {
		Object[] row = (Object[]) iter.next();            
	    Integer oid = (Integer) row[0];              
		String ruta = (String) row[1]; 
		String imagen = (String) row[2]; 
		int coincidencias = (int) row[3];
		if (log.isInfoEnabled()) log.info("coincidencias:" + coincidencias);
			
		if(coincidencias >= 10){
			idObjeto = oid; 
		} else {
			idObjeto = 999; //no correspondecia 
		}
	}

	if (log.isInfoEnabled()) log.info("Identificador Objeto:" + idObjeto);
	if (log.isInfoEnabled()) log.info("* FIN BuscarResultadoImagen.groovy *******************************");
	if (log.isInfoEnabled()) log.info("******************************************************************");

	return ["resultCode":"success", "idObjeto":idObjeto, "fecha":fecha, "imagen":imagen, "nombreImagen":nombreImagen]

}catch(e){
	if (log.isInfoEnabled()) log.info("ERROR:" + e);
	return ["resultCode":"error"]
}