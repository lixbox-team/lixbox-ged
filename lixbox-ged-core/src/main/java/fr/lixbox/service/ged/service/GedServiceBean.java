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
package fr.lixbox.service.ged.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.ged.Constant;
import fr.lixbox.service.ged.GedService;
import fr.lixbox.service.ged.model.GedEntry;
import fr.lixbox.service.ged.model.GedFileEntry;
import fr.lixbox.service.ged.provider.GedProvider;
import fr.lixbox.service.ged.provider.RedisProvider;
import fr.lixbox.service.param.ParametreServiceClient;
import fr.lixbox.service.param.model.Parametre;
import fr.lixbox.service.registry.RegistryService;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.ServiceEntry;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import redis.clients.jedis.Jedis;

/**
 * Cette classe implemente le service de ged
 * 
 * @author ludovic.terral
 */
@RolesAllowed({"READ_DOC","WRITE_DOC"})
public class GedServiceBean implements Serializable, GedService
{
	// ----------- Attribut -----------
	private static final long serialVersionUID = -201711101344L;
    private static final Log LOG = LogFactory.getLog(GedService.class);
    private static final String SERVICE_REDIS_TEXT = "LE SERVICE REDIS ";
    
    @Inject private RedisProvider redisProvider;
    
    @Inject @LocalRegistryConfig RegistryService registryService;
    @Inject @LocalRegistryConfig ParametreServiceClient parametreService;



    // ----------- Methode -----------
    @Override
    @PermitAll
    public ServiceState checkHealth() 
    {
        return checkReady();
    }

    
    
    @Override
    @PermitAll
    public ServiceState checkReady()
    {
        LOG.debug("Check Health started");
        ServiceState state = new ServiceState();
        
                
        //controle de redis
        String redisUri = "";
        try
        {
            ServiceEntry redis = registryService.discoverService(Constant.REDIS_NAME, Constant.REDIS_VERSION);
            if (redis!=null)
            {
                redisUri = redis.getPrimary().getUri();
            }
            if (!StringUtil.isEmpty(redisUri))
            {
                String hostName = redisUri.substring(6,redisUri.lastIndexOf(':'));
                String port = redisUri.substring(redisUri.lastIndexOf(':')+1);
                try (
                    Jedis redisClient = new Jedis(hostName, Integer.parseInt(port));
                )
                {           
                    redisClient.ping();
                    state.setStatus(ServiceStatus.UP);
                    LOG.debug(SERVICE_REDIS_TEXT+redisUri+" EST DISPONIBLE");
                }
                catch (Exception e)
                {
                    LOG.fatal(e,e);
                    LOG.error(SERVICE_REDIS_TEXT+redisUri+" N'EST PAS DISPONIBLE");
                    state.setStatus(ServiceStatus.DOWN);
                    state.getChecks().add(new Check(ServiceStatus.DOWN, SERVICE_REDIS_TEXT+redisUri+" N'EST PAS DISPONIBLE"));
                }
            }
            else
            {
                state.setStatus(ServiceStatus.DOWN);
                state.getChecks().add(new Check(ServiceStatus.DOWN, "IMPOSSIBLE DE TROUVER LE SERVICE REDIS"));
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
            state.getChecks().add(new Check(ServiceStatus.DOWN, SERVICE_REDIS_TEXT+redisUri+" N'EST PAS DISPONIBLE"));
        }
        
        
        //controle des parametres
        try
        {
            parametreService.getVersion();
            state.getChecks().add(new Check(ServiceStatus.UP, "LE SERVICE PARAMETRE EST DISPONIBLE"));
        }
        catch (Exception e)
        {
            state.getChecks().add(new Check(ServiceStatus.DOWN, "LE SERVICE PARAMETRE N'EST PAS DISPONIBLE"));
        }
        return state;
    }
    
    

    @Override 
    @PermitAll
    public ServiceState checkLive() 
    {
        return new ServiceState(ServiceStatus.UP);
    }



    /**
     * Cette methode renvoie la version courante du code. 
     */
    @Override
    @PermitAll
    public String getVersion()
    {   
        return CodeVersionUtil.getVersion(this.getClass());
    }

    
        
    
    /**
     * Cette methode va recuperer un document par son id.
     * 
     * @param serviceId
     * @param oid
     * 
     * @return le document demandé
     * 
     * @throws BusinessException
     */
    @Override
    public GedEntry getDocumentById(String serviceId, String oid)
        throws BusinessException
    {
        GedEntry entry = null;
        try
        {
            Map<String, String> hParams = getServiceParams(serviceId);
            GedProvider provider = getGedProvider(hParams);
            entry = provider.getDocumentById(serviceId, hParams, oid, true);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, GedService.SERVICE_CODE, true);
        }
        return entry;
    }



    /**
     * Cette methode recherche les documents correspondants aux rubriques fournies 
     * 
     * @param serviceId
     * @param criteria
     * 
     * @return les documents correspondants à la recherche
     * 
     * @throws BusinessException 
     */
    @Override
    public List<GedEntry> getDocumentsByCriterias(String serviceId, GedEntry criteria) throws BusinessException
    {
        List<GedEntry> result = null;
        Map<String, String> hParams = getServiceParams(serviceId);
        GedProvider provider = getGedProvider(hParams);
        result = provider.getDocumentsByCriterias(serviceId, hParams, criteria);
        if (CollectionUtil.isEmpty(result))
        {
            throw new BusinessException(LixboxResources.getString("MSG.ERROR.EXCEPUTI_01", GedService.SERVICE_CODE));
        }
        return result;
    }

    
    
    /**
     * Cette methode assure l'association de deux dossiers.
     * 
     * @param serviceId
     * @param oidDoc1
     * @param oidDoc2
     * 
     * @throws BusinessException 
     */
    @Override
    @RolesAllowed({"WRITE_DOC","ADMIN"})
    public boolean associerDocuments(String serviceId, String oidDoc1, String oidDoc2) 
            throws BusinessException
    {
        Map<String, String> hParams = getServiceParams(serviceId);
        GedProvider provider = getGedProvider(hParams);
        return provider.associerDocuments(serviceId, hParams, oidDoc1, oidDoc2);
    }



    /**
     * Cette méthode assure la persistence d'un document
     * 
     * @param serviceId
     * @param document
     * @param syncFileEntry
     * 
     * @return le document mergé.
     * 
     * @throws BusinessException
     */
    @Override
    @RolesAllowed({"WRITE_DOC","ADMIN"})
    public GedEntry synchronize(String serviceId, GedEntry document, boolean syncFileEntries) throws BusinessException
    {
        Map<String, String> hParams = getServiceParams(serviceId);
        GedProvider provider = getGedProvider(hParams);
        return provider.synchronize(serviceId, hParams, document, syncFileEntries);
    }

    
    
    /**
     * Cette methode assure la suppression d'un document dans la GED.
     * 
     * @param serviceId
     * @param oid
     * 
     * @return true si la suppression est effective.
     * 
     * @throws BusinessException
     */
    @Override
    @RolesAllowed({"WRITE_DOC","ADMIN"})
    public boolean remove(String serviceId, String oid) throws BusinessException
    {
        Map<String, String> hParams = getServiceParams(serviceId);
        GedProvider provider = getGedProvider(hParams);
        return provider.remove(serviceId, oid, hParams);
    }



    @Override
    @PermitAll
    public Response getFileEntryByAppIdGedIdFileEntryId(String serviceId, String gedOid, String fileEntryOid) 
        throws BusinessException
    {
        GedEntry gedEntry = getDocumentById(serviceId, gedOid);
        Optional<GedFileEntry> fileEntry = gedEntry.getEntries().stream().filter(file-> fileEntryOid.equals(file.getOid())).findFirst();
                
        Response result;
        if (fileEntry.isPresent())
        {
            result = Response
                .status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST")
                .type(fileEntry.get().getMimeType())
                .encoding("UTF-8")
                .entity(fileEntry.get().getContent())
                .build();
        }
        else
        {
            result = Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(LixboxResources.getString("MSG.ERROR.EXCEPUTI_01", GedService.SERVICE_CODE))
                    .build();
        }
        return result;
    }

    

    private Map<String, String> getServiceParams(String serviceId) throws BusinessException
    {
        Map<String, String> hParams = new HashMap<>();
        List<Parametre> params = parametreService.getParametresByService(serviceId);
        for (Parametre param : params)
        {
            hParams.put(param.getCode(), param.getValue());
        }
        return hParams;
    }



    private GedProvider getGedProvider(Map<String, String> hParams)
    {
        GedProvider provider = redisProvider;
        return provider;
    }
}