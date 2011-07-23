package es.reala.redireccion;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wurfl.core.CapabilityNotDefinedException;
import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.WURFLHolder;
import net.sourceforge.wurfl.core.WURFLManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RedireccionUsuario extends HttpServlet {

 	private static final long serialVersionUID = 1L;
 	
 	/** Constantes jsp's a redireccionar **/
 	private final String jspNormal = "indexNormalUsuario.jsp";
 	private final String jspMobile = "indexMobileUsuario.jsp";
 	
 	/** Logger */
    private final Log log = LogFactory.getLog(getClass());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
	        WURFLHolder wurflHolder = (WURFLHolder) getServletContext().getAttribute("net.sourceforge.wurfl.core.WURFLHolder");
	
	        WURFLManager wurfl = wurflHolder.getWURFLManager();
	
	        Device device = wurfl.getDeviceForRequest(request);
	                  
	        String userAgent = device.getUserAgent();
	
	        String isWirelessDevice = device.getCapability("is_wireless_device");
	        String deviceClaimsWebSupport = device.getCapability("device_claims_web_support");

	        if (log.isInfoEnabled()) log.info("******************************************************************");
	        if (log.isInfoEnabled()) log.info("* RedireccionUsuario.processRequest*******************************");
	        if (log.isInfoEnabled()) log.info("Device: " + device.getId());
	        if (log.isInfoEnabled()) log.info("isWirelessDevice: " + isWirelessDevice);
	        if (log.isInfoEnabled()) log.info("deviceClaimsWebSupport: " + deviceClaimsWebSupport);
	        if (log.isInfoEnabled()) log.info("userAgent: " + userAgent);
	        
	        if(isWirelessDevice.equalsIgnoreCase("false") && deviceClaimsWebSupport.equalsIgnoreCase("true")) {
	        	request.getRequestDispatcher(jspNormal).forward(request,response);
		        if (log.isInfoEnabled()) log.info("redireccion: " + jspNormal);
	        	
	        } else {
	        	request.getRequestDispatcher(jspMobile).forward(request,response);

	        	if (log.isInfoEnabled()) log.info("redireccion: " + jspMobile);
	        }	        
	        
	        if (log.isInfoEnabled()) log.info("* FIN RedireccionUsuario.processRequest****************************");
	        if (log.isInfoEnabled()) log.info("******************************************************************");

        } catch (CapabilityNotDefinedException e) {
        	throw new RuntimeException("Somethingh is seriously wrong with your WURFL:" + e.getLocalizedMessage(), e);
        }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }
}
