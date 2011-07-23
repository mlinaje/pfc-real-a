//outputs=idIdioma
import com.webratio.rtx.db.DBTransaction
import com.webratio.rtx.db.HibernateService
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.webratio.rtx.RTXConstants

def leng = sessionContext.get(RTXConstants.LANGUAGE_CTX_PARAM_KEY);
def pais = sessionContext.get(RTXConstants.COUNTRY_CTX_PARAM_KEY);
def idIdioma = null;
Log log = LogFactory.getLog(this.getClass());

if (log.isInfoEnabled()) log.info("******************************************************************");
if (log.isInfoEnabled()) log.info("* ObtenerIdiomaActual.groovy ******************************");
if (log.isInfoEnabled()) log.info("Variables de sesión:");
if (log.isInfoEnabled()) log.info("Lenguaje:" + leng);
if (log.isInfoEnabled()) log.info("Pais:" + pais);


def dbId = "db1";
def session = getDBSession(dbId);

//Realiza la busqueda de las imagenes
def select = "SELECT oid FROM idioma" 
if((null != leng && "" != leng) || (null != pais && "" != pais)){
	select = select + " WHERE"
	if(null != leng && "" != leng){
		select = select + " language = :idioma ";
	}
	if((null != leng && "" != leng) && (null != pais && "" != pais)){
		select = select + " AND"
	}
	if(null != pais && "" != pais){
		select = select + " country = :pais";
	}
}

if (log.isInfoEnabled()) log.info("Query:" + select);
	  			 
def querySeleccion = session.createSQLQuery(select);
if(null != leng && "" != leng){
	querySeleccion.setParameter("idioma", leng);
}
if(null != pais && "" != pais){
	querySeleccion.setParameter("pais", pais);
}

idIdioma = querySeleccion.uniqueResult();

if (log.isInfoEnabled()) log.info("SALIDA:");
if (log.isInfoEnabled()) log.info("idIdioma:" + idIdioma);
if (log.isInfoEnabled()) log.info("* FIN ObtenerIdiomaActual.groovy **************************");
if (log.isInfoEnabled()) log.info("******************************************************************");

return ["resultCode":"success", "idIdioma":idIdioma]
