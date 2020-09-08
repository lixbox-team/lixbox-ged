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
package fr.lixbox.service.ged.provider;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.stream.util.FileUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.SearchQueryHelper;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.ged.GedService;
import fr.lixbox.service.ged.model.GedEntry;
import fr.lixbox.service.ged.model.GedFileEntry;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;


/**
 * Ce provider gere la ged interne.
 *  
 * @author ludovic.terral
 */
@ApplicationScoped
@RolesAllowed({"READ_DOC","WRITE_DOC"})
public class RedisProvider implements Serializable, GedProvider
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = -201711131512L;
    private static final Log LOG = LogFactory.getLog(RedisProvider.class);
    private static final String DEFAULT_STORAGE_PATH = System.getProperty("user.home").replace('\\', '/') + "/internal/storage/";
    private static final String STORAGE_PATH_KEY = "STORAGE_PATH";
    
    @ConfigProperty(name="storage.path", defaultValue="") String storagePath;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    
    
    // ----------- Methode(s) -----------
    @Override
    public GedEntry getDocumentById(String serviceId, Map<String, String> hParams, String oid, boolean loadContent) throws BusinessException
    {
        String extractedPathStorage = extractStoragePath(hParams);
        if (!new File(extractedPathStorage).exists())
        {
            LOG.trace(new File(extractedPathStorage).mkdirs());
        }
        GedEntry result = null;
        try
        {
            result = redisClient.findById(GedEntry.class, oid);
            if (loadContent)
            {
                chargerDependancesGedFileEntries(result);
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, GedService.SERVICE_CODE, true);
        }
        return result;
    }
    
    
    
    @Override
    public List<GedEntry> getDocumentsByCriterias(String serviceId, Map<String, String> hParams, GedEntry criteria) 
        throws BusinessException
    {
        String extractedStoragePath = extractStoragePath(hParams);
        if (!new File(extractedStoragePath).exists())
        {
            LOG.trace(new File(extractedStoragePath).mkdirs());
        }
        List<GedEntry> result = null;
        try
        {                     
            result = redisClient.findByExpression(GedEntry.class, SearchQueryHelper.toQueryByCriteria(criteria));
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, GedService.SERVICE_CODE, true);
        }
        return result;
    }
    


    @Override
    public GedEntry synchronize(String serviceId, Map<String, String> hParams, GedEntry document, boolean syncFileEntry) throws BusinessException
    {
        GedEntry result = null;
        try
        {
            String extractedStoragePath = extractStoragePath(hParams);
 
            //preparation du document
            document.setServiceId(serviceId);
            if (StringUtil.isEmpty(document.getOid()))
            {
                document.setOid(GuidGenerator.getGUID(document));
            }
            
            //ecriture des fichiers
            if (syncFileEntry)
            {
                for (GedFileEntry entry:document.getEntries())
                {
                    if (StringUtil.isEmpty(entry.getOid()))
                    {
                        entry.setOid(GuidGenerator.getGUID(document));
                    }
                    entry.setFileName(entry.getOid()+".data");
                    entry.setStoragePath(extractedStoragePath+"/"+document.getServiceId()+"/"+document.getEntite());
                    if (!new File(entry.getStoragePath()).exists())
                    {
                        LOG.trace(new File(entry.getStoragePath()).mkdirs());
                    }            
                    Files.write(Paths.get(entry.getStoragePath()+"/"+entry.getFileName()).toFile().toPath(), entry.getContent());
                }
            }
            result = redisClient.merge(document);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, GedService.SERVICE_CODE, true);
        }
        return result;
    }
    
    
    

    @Override
    public boolean remove(String serviceId, String oid, Map<String, String> hParams) throws BusinessException
    {
        boolean result = false;
        try
        {
            
            GedEntry entry = redisClient.findById(GedEntry.class, oid);
            for (GedFileEntry fileEntry:entry.getEntries())
            {
               File file = Paths.get(fileEntry.getStoragePath()+"/"+fileEntry.getFileName()).toFile();
               if (file.exists())
               {
                   FileUtil.forceDelete(file);
               }
            }            
            redisClient.remove(GedEntry.class,oid);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, GedService.SERVICE_CODE, true);
        }
        return result;
    }
    

    
    @Override
    public boolean associerDocuments(String serviceId, Map<String, String> hParams,
            String oidDoc1, String oidDoc2) throws BusinessException
    {
        throw new ProcessusException("NOT YET IMPLEMENTED");
    }
    
    
    
    private String extractStoragePath(Map<String, String> hParams)
    {
        return StringUtil.isNotEmpty(hParams.get(STORAGE_PATH_KEY))?
                hParams.get(STORAGE_PATH_KEY):
                    (StringUtil.isNotEmpty(storagePath)&&!"not use".equals(storagePath))?
                            storagePath:
                                DEFAULT_STORAGE_PATH;
        
    }

    
    
    /**
     * Cette methode charge les dependances d'un GedEntry
     * 
     * @param entity
     *     
     * @return l'entite chargee
     * 
     */    
     private GedEntry chargerDependancesGedFileEntries(GedEntry entity)
     {
         if (entity != null)
         {
             for (GedFileEntry fileEntry : entity.getEntries())
             {
                 try
                 {
                     fileEntry.setContent(Files.readAllBytes(Paths.get(fileEntry.getStoragePath() + "/" + fileEntry.getFileName())));
                 }
                 catch (IOException e)
                 {
                     LOG.error(LixboxResources.getString("ERROR.DOC.LECT", e));
                 }
             }
         }
         return entity;
     }
}