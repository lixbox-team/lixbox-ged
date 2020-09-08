/*******************************************************************************
 *    
 *                           FRAMEWORK Lixbox
 *                          ==================
 *      
 *    This file is part of lixbox-service.
 *
 *    lixbox-ged is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    lixbox-ged is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with lixbox-ged.  If not, see <https://www.gnu.org/licenses/>
 *   
 *   @AUTHOR Lixbox-team
 *
 ******************************************************************************/
package fr.lixbox.service.ged;

import java.net.InetAddress;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import fr.lixbox.service.registry.model.ServiceType;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Cette classe enregistre le service dans le registry service.
 * 
 * @author ludovic.terral
 */
@Singleton
public class GedServiceRegister 
{
    // ----------- Attribut(s) -----------  
    private static final Log LOG = LogFactory.getLog(GedServiceRegister.class);
    private static final String UNABLE_TO_REGISTER_TXT = "UNABLE TO REGISTER "; 
    
    @Inject @LocalRegistryConfig RegistryServiceClient registryClient;
    @ConfigProperty(name="quarkus.http.port") int hostPort;
    private String endpointURI;

    

    // ----------- Methode(s) -----------
    public void registerService(@Observes StartupEvent ev)
    {
        try
        {
            InetAddress inetAddress = InetAddress.getLocalHost();
            endpointURI = "http://" + inetAddress.getHostAddress()+ ":" + hostPort + GedService.FULL_SERVICE_URI;
            boolean result = registryClient.registerService(GedService.SERVICE_NAME, GedService.SERVICE_VERSION, ServiceType.MICRO_PROFILE, endpointURI);
            LOG.info("SERVICE GedService REGISTRATION IS "+result+" ON "+registryClient.getCurrentRegistryServiceUri());
        }
        catch(NullPointerException e)
        {
            LOG.info(UNABLE_TO_REGISTER_TXT+GedService.SERVICE_NAME+"-"+GedService.SERVICE_VERSION+": absence d'annuaire");
        }
        catch(Exception e)
        {
            LOG.error(UNABLE_TO_REGISTER_TXT+GedService.SERVICE_NAME+"-"+GedService.SERVICE_VERSION+": "+ExceptionUtils.getRootCauseMessage(e));
            LOG.error(e);
        }
    }
    
    
    
    public void unregisterService(@Observes ShutdownEvent ev)
    {
        try
        {
            boolean result = registryClient.unregisterService(GedService.SERVICE_NAME, GedService.SERVICE_VERSION, endpointURI);
            LOG.info("SERVICE GedService UNREGISTRATION IS "+result+" ON "+registryClient.getCurrentRegistryServiceUri());
        }
        catch(Exception e)
        {
            LOG.error(UNABLE_TO_REGISTER_TXT+GedService.SERVICE_NAME+"-"+GedService.SERVICE_VERSION+": "+ExceptionUtils.getRootCauseMessage(e));
            LOG.trace(e);
        }
    }
}