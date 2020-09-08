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
package fr.lixbox.service.ged.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;

/**
 * Ce filtre supprime les données de l'api qui ne doivent pas être diffusées
 * 
 * @author ludovic.terral
 */
public class CommonOASFilter implements OASFilter
{
    // ----------- Methode(s) -----------
    @Override
    public void filterOpenAPI(OpenAPI openAPI) 
    {
        // registry client

        openAPI.getPaths().removePathItem("/ged/api/1.0/health");
        openAPI.getPaths().removePathItem("/ged/api/1.0/health/live");
        openAPI.getPaths().removePathItem("/ged/api/1.0/health/ready");
        openAPI.getPaths().removePathItem("/ged/api/1.0/version");
        openAPI.getPaths().removePathItem("/ged/api/1.0/clear");
        openAPI.getPaths().removePathItem("/ged/api/1.0/discover/{name}/{version}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/entries");
        openAPI.getPaths().removePathItem("/ged/api/1.0/entries/{name}");
        
        // dbservice
        openAPI.getPaths().removePathItem("/ged/api/1.0/push/db/redis");
        openAPI.getPaths().removePathItem("/ged/api/1.0/push/db/redis/{id}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/push/redis/db");
        openAPI.getPaths().removePathItem("/ged/api/1.0/push/redis/db/{id}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/remove/db/{id}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/remove/redis/{id}");
        
        
        
        // cache client
        openAPI.getPaths().removePathItem("/ged/api/1.0/keys");
        openAPI.getPaths().removePathItem("/ged/api/1.0/keys/contains");
        openAPI.getPaths().removePathItem("/ged/api/1.0/keys/size");
        openAPI.getPaths().removePathItem("/ged/api/1.0/keys/{key}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/value/{key}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/values");
        
        openAPI.getPaths().removePathItem("/ged/api/1.0/register/{name}/{version}");
        openAPI.getPaths().removePathItem("/ged/api/1.0/unregister/{name}/{version}");
        
        //components
        Map<String, Schema> temp = new HashMap<>(openAPI.getComponents().getSchemas());
        temp.remove("ServiceInstance");
        temp.remove("ListServiceInstance");
        temp.remove("ServiceTypestring");
        temp.remove("ServiceEntry");
        temp.remove("ListServiceEntry");
        temp.remove("ServiceTypestring");
        temp.remove("ServiceType");
        openAPI.getComponents().setSchemas(temp);
        openAPI.getComponents().getSchemas().get("MapStringString").setTitle("CheckDatas");
    }
}
