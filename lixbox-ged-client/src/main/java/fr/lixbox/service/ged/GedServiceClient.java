/*******************************************************************************
 *    
 *                           FRAMEWORK Lixbox
 *                          ==================
 *      
 *    This file is part of lixbox-ged.
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

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.service.common.client.MicroServiceClient;
import fr.lixbox.service.ged.model.GedEntry;

/**
 * Cette classe est le client d'accès au ged-service.
 * 
 * @author ludovic.terral
 */
public class GedServiceClient extends MicroServiceClient implements GedService
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = 201707111611L;
    private static final Log LOG = LogFactory.getLog(GedServiceClient.class);

    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String MSG_ERROR_EXCEPUTI_09 = "MSG.ERROR.EXCEPUTI_09";
    private static final String APP_ID = "appId";
    
    
    
    // ----------- Methode(s) -----------   
    public GedServiceClient()
    {
        init();
    }
    public GedServiceClient(String serviceRegistryUri)
    {
        init(serviceRegistryUri);
    }
    @Override
    protected void loadInfosService()
    {
        serviceName = GedService.SERVICE_NAME;
        serviceVersion = GedService.SERVICE_VERSION;
    }

    
    
    @Override
    public Response getFileEntryByAppIdGedIdFileEntryId(String appOid, String gedOid, String fileEntryOid) 
        throws BusinessException
    {
        if (appOid == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "appOid" }));
        }
        if (gedOid == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "gedOid" }));
        }
        if (fileEntryOid == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "fileEntryOid" }));
        }
        
        Response result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            result = service
                    .path(appOid).path(gedOid).path(fileEntryOid)
                    .request().get();
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                        new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    

    
    @Override
    public GedEntry getDocumentById(String appId, String oid) throws BusinessException
    {
        if (oid == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "oid" }));
        }
        if (appId == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, APP_ID }));
        }
        
        GedEntry result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            Response response = service
                    .path(appId).path(oid)
                    .request().get();
            result = (GedEntry) parseResponse(response, new GenericType<GedEntry>(){});
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                        new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    
    

    @Override
    public List<GedEntry> getDocumentsByCriterias(String appId, GedEntry criteria)
        throws BusinessException
    {
        if (appId == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, APP_ID }));
        }
        if (criteria == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "criteria" }));
        }
        
        
        List<GedEntry> result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            Response response = service
                    .path(appId).path("criterias")
                    .request().post(Entity.json(criteria.toString()));
            result = (List<GedEntry>) parseResponse(response, new GenericType<List<GedEntry>>(){});
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                        new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    
    
    
    @Override
    public GedEntry synchronize(String appId, GedEntry document, boolean syncFileEntries) throws BusinessException
    {
        if (appId == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, APP_ID }));
        }
        if (document == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "document" }));
        }
        
        GedEntry result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            Response response = service
                    .path(appId).path("sync").path(Boolean.toString(syncFileEntries))
                    .request().post(Entity.json(document.toString()));
            result = (GedEntry) parseResponse(response, new GenericType<GedEntry>(){});
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                    new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    
    
    
    @Override
    public boolean remove(String appId, String oid) throws BusinessException
    {
        if (appId == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, APP_ID }));
        }
        if (oid == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "oid" }));
        }
        
        Boolean result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            Response response = service
                    .path(appId).path(oid)
                    .request().delete();
            result = (Boolean) parseResponse(response, new GenericType<Boolean>(){});
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                        new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    


    @Override
    public boolean associerDocuments(String appId, String oidDoc1, String oidDoc2) 
        throws BusinessException
    {
        if (appId == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, APP_ID }));
        }
        if (oidDoc1 == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "oidDoc1" }));
        }
        if (oidDoc2 == null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02,
                    new String[] { GedServiceClient.SERVICE_CODE, "oidDoc2" }));
        }
        
        
        boolean result;
        WebTarget service = getSecureService();
        if (service != null)
        {
            Response response = service
                    .path(appId).path("couple").queryParam("oidDoc1",oidDoc1).queryParam("oidDoc2",oidDoc2)
                    .request().put(Entity.entity("true", MediaType.APPLICATION_JSON));
            result = (boolean) parseResponse(response, new GenericType<Boolean>(){});
        }
        else
        {
            throw new ProcessusException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09,
                        new String[] { GedService.SERVICE_CODE, GedService.SERVICE_NAME }));
        }
        return result;
    }
    

    
    @Override
    protected void syncCache()
    {
        initStore();
    }
    
    

    @Override
    protected void initStore()
    {
        LOG.debug("NO CACHE ON GED SERVICE");
    }
    
    

    @Override
    protected void loadCache()
    {       
        initStore();
    }
}