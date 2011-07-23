//inputs=id
//outputs=id|Language|Country
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import com.webratio.rtx.core.BeanHelper
import org.apache.commons.lang.math.NumberUtils
import com.webratio.rtx.RTXConstants
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* CambiarIdioma.groovy********************************************");
if (log.isInfoEnabled()) log.info("ENTRADA:");
if (log.isInfoEnabled()) log.info("id:" + id);

def dbId = "db1";
def session = getDBSession(dbId);
language = null;              
country = null; 

def idio = null;
def idioOID = null;
if(null != id){
	idio = BeanHelper.asString(id);
	idioOID = NumberUtils.toInt(idio);
}

//Realiza la busqueda de las imagenes
def select = "SELECT language ,country FROM \"idioma\" WHERE oid = :idiomaOid";    
               			 
def querySeleccion = session.createSQLQuery(select);
querySeleccion.setParameter("idiomaOid", idioOID);
def result = querySeleccion.list();

//Genera la tabla temporal con los registros de la query
for (Iterator iter = result.iterator(); iter.hasNext();) {
	Object[] row = (Object[]) iter.next();            
    language = (String) row[0];              
	country = (String) row[1]; 
	sessionContext.put(RTXConstants.LANGUAGE_CTX_PARAM_KEY, language);
	sessionContext.put(RTXConstants.COUNTRY_CTX_PARAM_KEY, country);
}    

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("id: " + id);
if (log.isInfoEnabled()) log.info("Language: " + language);
if (log.isInfoEnabled()) log.info("Country: " + country);
if (log.isInfoEnabled()) log.info("* FIN CambiarIdioma.groovy ***************************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "id":id, "Language":language, "Country":country]
