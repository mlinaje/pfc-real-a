//inputs=imagenEntrada

import com.webratio.rtx.core.BeanHelper
import org.apache.commons.lang.math.NumberUtils
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import com.webratio.rtx.vdb.VirtualEntityTable
import com.webratio.webapp.TmpBusqueda
import com.webratio.webapp.TmpComparacion
import com.webratio.rtx.RTXBLOBData;
import com.webratio.rtx.blob.BLOBData;
import org.apache.commons.io.FilenameUtils
import com.webratio.units.content.rtx.blob.UploadedBLOBData
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ObtenerQueryImagenes.groovy ************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("imagenEntrada:" + imagenEntrada);

String rutaQuery = "/" + FilenameUtils.separatorsToUnix(FilenameUtils.getPath(rtx.getUploadDirectory()));
String imagenQuery = "" + imagenEntrada;
String pathCompleto = rutaQuery + imagenEntrada;

pathCompleto = pathCompleto.replace("{", "");
pathCompleto = pathCompleto.replace("}", "");

imagenQuery = imagenQuery.replace("{", "");
imagenQuery = imagenQuery.replace("}", "");

if (log.isInfoEnabled()) log.info("rutaQuery:" + rutaQuery);
if (log.isInfoEnabled()) log.info("imagenQuery:" + imagenQuery);
if (log.isInfoEnabled()) log.info("pathCompleto:" + pathCompleto);

def dbId = "db1";
def session = getDBSession(dbId);

def tmpBusquedaEntityId = "ent5";
VirtualEntityTable tmpBusquedaTable = new VirtualEntityTable(tmpBusquedaEntityId, sessionContext, service)

//Borrado de la tabla temporal
tmpBusquedaTable.delete(tmpBusquedaTable.getRows());

def tmpComparacionEntityId = "ent7";
VirtualEntityTable tmpComparacionTable = new VirtualEntityTable(tmpComparacionEntityId, sessionContext, service)

//Borrado de la tabla temporal
tmpComparacionTable.delete(tmpComparacionTable.getRows());

//Realiza la busqueda de las imagenes
def select = "SELECT o.oid, o.descripcion, " +
			 "p.ruta, p.imagen, " +
  			 "numero_de_coincidencias(p.oid,:pathCompleto) AS coincidencias " + 
  			 "FROM \"plantilla\" AS p " +
  			 "INNER JOIN \"objeto\" AS o ON o.oid = p.objeto_oid " + 
  			 "ORDER BY coincidencias DESC LIMIT 10";
  			 
def querySeleccion = session.createSQLQuery(select);
querySeleccion.setParameter("pathCompleto", pathCompleto);
def result = querySeleccion.list();

//Genera la tabla temporal con los registros de la query
for (Iterator iter = result.iterator(); iter.hasNext();) {
	Object[] row = (Object[]) iter.next();            
	Integer idObjeto = (Integer) row[0]; 
  	String descripcion = (String) row[1];              
	String ruta = (String) row[2]; 
	String imagen = (String) row[3]; 
	Integer coincidencias = (Integer) row[4];

  	RTXBLOBData blobFile = new BLOBData(imagen, rtx);
 	RTXBLOBData blobImagenEntrada = new BLOBData(imagenQuery, rtx);
	
	TmpBusqueda tmpBusqueda = new TmpBusqueda();
  	tmpBusqueda.setIdobjeto(idObjeto);
 	tmpBusqueda.setDescripcion(descripcion);
  	tmpBusqueda.setRuta(ruta);
  	tmpBusqueda.setImagen(blobFile);
  	tmpBusqueda.setCoincidencias(coincidencias);
  	tmpBusqueda.setRutaquery(rutaQuery);    
  	tmpBusqueda.setImagenquery(blobImagenEntrada);
    
	tmpBusquedaTable.save(tmpBusqueda);
}    

if (log.isInfoEnabled()) log.info("* FIN ObtenerQueryImagenes.groovy ********************************");
if (log.isInfoEnabled()) log.info("******************************************************************");
       
return ["resultCode":"success"]
