package es.reala.uploadfile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webratio.rtx.core.DefaultRTXManager;
import com.webratio.struts.WRGlobals;
 
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/** Logger */
	private final Log log = LogFactory.getLog(getClass());
	/** Constantes */
	private static final String TMP_DIR_PATH = "/tmp";
//	private static final String DESTINATION_DIR_PATH ="/files";

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (log.isInfoEnabled()) log.info("******************************************************************");
		if (log.isInfoEnabled()) log.info("* UploadFileSerlevlet.doPost *************************************");

		ServletContext contexto = this.getServletContext();
		DefaultRTXManager defaultRTXManager = (DefaultRTXManager) contexto.getAttribute(WRGlobals.RTX_KEY); 
				
		PrintWriter out = response.getWriter();
		
		response.setContentType("text/html");

		int estado = HttpServletResponse.SC_OK;
		String textoEstado = "OK";
		
		String realPathTemporal = getServletContext().getRealPath(TMP_DIR_PATH);
		File tmpDir = new File(realPathTemporal);
		if(!tmpDir.isDirectory()) {
			estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			textoEstado = "INTERNAL_SERVER_ERROR";
	        response.setStatus(estado);
	        out.write(textoEstado);
			out.close();
			throw new ServletException(TMP_DIR_PATH + " no es un directorio");
		}

		String realPathDestino = defaultRTXManager.getUploadDirectory();
		File destinationDir = new File(realPathDestino);
		if(!destinationDir.isDirectory()) {
			estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			textoEstado = "INTERNAL_SERVER_ERROR";
	        response.setStatus(estado);
	        out.write(textoEstado);
			out.close();
			throw new ServletException(realPathDestino + " no es un directorio");
		}

 
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();

		//Umbral de tamaño, por encima del cual el contenido se almacena en el disco.
		fileItemFactory.setSizeThreshold(10*1024*1024); //1 MB

		//Directorio temporal para almacenar ficheros por encima del umbral
		fileItemFactory.setRepository(tmpDir);
 
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			//Analiza la solicitud
			List items = uploadHandler.parseRequest(request);
			for (Iterator iterator = items.iterator(); iterator.hasNext();) {
				FileItem item = (FileItem) iterator.next();

				if(item.isFormField()) {
					//Se controlan los campos de formulario
					if (log.isInfoEnabled()) log.info("Nombre del campo...:" + item.getFieldName());
					if (log.isInfoEnabled()) log.info("Valor..............:" + item.getString());
					
					String campo = null == item.getFieldName()?null:item.getFieldName().replaceAll("<data>", "");
					campo = null == campo?null:campo.replaceAll("</data>", "");
					String valor = null == item.getString()?null:item.getString().replaceAll("<data>", "");
					valor = null == valor?null:valor.replaceAll("</data>", "");
					if(null != campo && campo.equals("usuario")){
						destinationDir = new File(realPathDestino,valor);
						if(!destinationDir.exists()){
							if(!destinationDir.mkdir()){
								estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
								textoEstado = "INTERNAL_SERVER_ERROR";
						        response.setStatus(estado);
						        out.write(textoEstado);
								out.close();
								throw new ServletException("Error al crear directorio del usuario");
							}
						}
					}					
				} else {
					//Se manejan los ficheros a subir
					if (log.isInfoEnabled()) log.info("Nombre de campo....:" + item.getFieldName());
					if (log.isInfoEnabled()) log.info("Nombre de fichero..:" + item.getName());
					if (log.isInfoEnabled()) log.info("Tipo de contenido..:" + item.getContentType());
					if (log.isInfoEnabled()) log.info("Tamaño.............:" + item.getSize());

					//Se escribe el fichero al directorio de destino			
					File fichero = new File(item.getName());
					String nombreFichero = fichero.getName();  
					File file = new File(destinationDir, nombreFichero);
					item.write(file);
				}
			}
		}catch(FileUploadException ex) {
			estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			textoEstado = "INTERNAL_SERVER_ERROR";			
			if (log.isInfoEnabled()) log.error("Error encotrado durante el analisis de solititud. ",ex);
		} catch(Exception ex) {
			estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			textoEstado = "INTERNAL_SERVER_ERROR";			
			if (log.isInfoEnabled()) log.error("Error encontrado durante la descarga del fichero. ",ex);
		}

        response.setStatus(estado);
        out.write(textoEstado);
		out.close();
		
		if (log.isInfoEnabled()) log.info("* FIN UploadFileSerlevlet.doPost *********************************");
		if (log.isInfoEnabled()) log.info("******************************************************************");

	}
}