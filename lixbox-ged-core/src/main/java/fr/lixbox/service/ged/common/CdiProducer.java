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

import javax.enterprise.inject.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.ged.Constant;
import fr.lixbox.service.param.ParametreServiceClient;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import fr.lixbox.service.registry.model.ServiceEntry;

/**
 * Cette classe produit les objets CDI nécessaire au service
 * 
 * @author ludovic.terral
 */
public class CdiProducer
{
    // ----------- Attribut -----------
    private static final Log LOG = LogFactory.getLog(CdiProducer.class);
    
    private static final String DEFAULT_REGISTRY_URL = "http://localhost:18100/registry/api/1.0";    

    @ConfigProperty(name="registry.uri", defaultValue=DEFAULT_REGISTRY_URL) String registryUri;
    @ConfigProperty(name="redis.uri", defaultValue="") String redisUri;
        

    // ----------- Methode -----------
    @Produces @LocalRegistryConfig
    public RegistryServiceClient getRegistryServiceClient() 
    {
        RegistryServiceClient result = null;
        try
        {
            result = new RegistryServiceClient(registryUri);
        }
        catch(Exception e)
        {
            LOG.error(registryUri);
            LOG.fatal(e);
            //absence de service d'annuaire.
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ExtendRedisClient getExtendRedisClient() 
    {
        ExtendRedisClient result = null;
        try
        {
            if (StringUtil.isEmpty(redisUri)||"not_use".equals(redisUri)) 
            {
                ServiceEntry redis = getRegistryServiceClient().discoverService(Constant.REDIS_NAME, Constant.REDIS_VERSION);
                if (redis!=null)
                {
                    redisUri = redis.getPrimary().getUri();
                }
            }
            if (!StringUtil.isEmpty(redisUri))
            {
                String hostName = redisUri.substring(6,redisUri.lastIndexOf(':'));
                String port = redisUri.substring(redisUri.lastIndexOf(':')+1);
                result = new ExtendRedisClient(hostName, Integer.parseInt(port));
                result.open();
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ParametreServiceClient getParametreService() 
    {   
        ParametreServiceClient result = null;
        try
        {
            result = new ParametreServiceClient(registryUri);
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}