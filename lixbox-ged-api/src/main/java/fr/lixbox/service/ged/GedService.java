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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;
import fr.lixbox.service.ged.model.GedEntry;


/**
 * Cette interface définit le contrat du microservice ged.
 * 
 * @author ludovic.terral
 */
@Path(GedService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public interface GedService extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1201711071604L;
    
    static final String SERVICE_NAME = "global-service-api-ged";
    static final String SERVICE_CODE = "GEDSERV";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION;
    static final String FULL_SERVICE_URI = "/ged/api/"+SERVICE_URI;

    

    // ----------- Methodes -----------    
    @GET @Path("/{serviceId}/{gedId}") GedEntry getDocumentById(@PathParam("serviceId") String serviceId, @PathParam("gedId") String gedId) throws BusinessException;
    @POST @Path("/{serviceId}/criterias") List<GedEntry> getDocumentsByCriterias(@PathParam("serviceId") String serviceId, GedEntry criteria) throws BusinessException;
    
    @PUT  @Path("/{serviceId}/couple") boolean associerDocuments(@PathParam("serviceId") String serviceId, @QueryParam("oidDoc1") String oidDoc1, @QueryParam("oidDoc2") String oidDoc2) throws BusinessException;
    @POST @Path("/{serviceId}/sync/{syncFileEntries}") GedEntry synchronize(@PathParam("serviceId") String serviceId, GedEntry document, @PathParam("syncFileEntries") boolean syncFileEntries) throws BusinessException;
    @DELETE @Path("/{serviceId}/{gedId}") boolean remove(@PathParam("serviceId") String serviceId, @PathParam("gedId") String gedId) throws BusinessException;
    
    @GET @Path("/{serviceId}/{gedId}/{fileEntryOid}") Response getFileEntryByAppIdGedIdFileEntryId(@PathParam("serviceId") String serviceId, @PathParam("gedId") String gedId, @PathParam("fileEntryOid") String fileEntryOid) throws BusinessException;
}