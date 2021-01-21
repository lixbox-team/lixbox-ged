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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.stream.util.FileUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.NumberUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.ged.Constant;
import fr.lixbox.service.ged.model.FileExtension;
import fr.lixbox.service.ged.model.GedEntry;
import fr.lixbox.service.ged.model.GedFileEntry;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import jvulcain.JV_ConsultReponse;
import jvulcain.JV_Exception;
import jvulcain.JV_Int;
import jvulcain.JV_Pages;
import jvulcain.JV_StruCREATEIDX;
import jvulcain.JV_StruINFODOSSIER;
import jvulcain.JV_StruINFOMAJPAGE;
import jvulcain.JV_StruINFONUMDOC;
import jvulcain.JV_StruINFOPAGE;
import jvulcain.JV_StruLISTERUB;
import jvulcain.JV_StruLOCUTION;
import jvulcain.JV_StruLOGIN;
import jvulcain.JV_StruREP_LR;
import jvulcain.Vulcain;

/**
 * Ce provider sert à la négociation avec les geds 
 * de type Sesin Poséidon
 *  
 * @author ludovic.terral
 */
@ApplicationScoped
@RolesAllowed({"READ_DOC","WRITE_DOC"})
public class SesinProvider implements Serializable, GedProvider
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = -201711101744L;
    private static final Log LOG = LogFactory.getLog(SesinProvider.class);
    private static final String DEFAULT_SESIN_TMP_PATH = System.getProperty("java.io.tmpdir") + "/sesin/";
    
    public static final int READ_MODE = 0;
    public static final int WRITE_MODE = 2;
    
    
    @Inject @LocalRegistryConfig RegistryServiceClient registryClient;
            
    
    
    // ----------- Methode(s) -----------
    /**
     * Cette methode va recuperer un document par son id.
     * 
     * @param application
     * @param hParams
     * @param oid
     * @param dependencies
     * 
     * @return le document demandé
     * 
     * @throws BusinessException
     */
    @Override
    public GedEntry getDocumentById(String serviceId, Map<String, String> hParams, String oid, boolean loadContent) throws BusinessException
    {
        Vulcain sesinClient = getSesinClient(hParams, READ_MODE);
        if (!new File(DEFAULT_SESIN_TMP_PATH).exists())
        {
            LOG.trace(new File(DEFAULT_SESIN_TMP_PATH).mkdirs());
        }
        GedEntry result=new GedEntry();
        try
        {
            if (!NumberUtil.isParsable(oid))
            {
                result=null;
                return result;
            }
            int iNumDocPos = Integer.parseInt(oid);
            result.setOid(oid);
            result.setServiceId(serviceId);
            LOG.info("SESINPROV:getDocumentById: Document-----------------: " + oid);
                
            //Extraction des chemins des fichiers associes
            Vector<JV_StruLISTERUB> vecStruLISTERUB = new Vector<>();
            JV_StruINFONUMDOC infosDoc = new JV_StruINFONUMDOC();
            sesinClient.PosGetInfoNumDoc(iNumDocPos, infosDoc);    
            result.setEntite(infosDoc.getTypeDoc());
            sesinClient.PosGetAllIndexDoc(iNumDocPos, vecStruLISTERUB);    
            for (JV_StruLISTERUB rub : vecStruLISTERUB)
            {
                result.getMetadatas().put(rub.getCodeRub(),rub.szValeurRub);
            }
            
            //chargement des contenus.
            JV_StruINFOPAGE[] tabStInfoPage = sesinClient.PosListeInfoPage(iNumDocPos, (short) 0);
            if (tabStInfoPage.length > 0)
            {
                for (int j = 0; j < tabStInfoPage.length; j++)
                {
                    String tmpPath = DEFAULT_SESIN_TMP_PATH +"/" + oid + "_" + j + "." + tabStInfoPage[j].getExtension().toLowerCase();
                    try
                    {
                        sesinClient.PosDmdPage(JV_Pages.TYPE_NUM_DOC, iNumDocPos, tabStInfoPage[j].getNumPage(), (short) 0, tmpPath);
                        GedFileEntry fileEntry = new GedFileEntry();
                        fileEntry.setOid(Integer.toString(j));
                        if (loadContent)
                        {
                            byte[] datas = Files.readAllBytes(Paths.get(new File(tmpPath).toURI()));
                            fileEntry.setContent(datas);
                            fileEntry.setMimeType(FileUtil.getMIMEType(datas));
                            fileEntry.setFileName(tabStInfoPage[j].getNomFichierOriginal());
                            result.getEntries().add(fileEntry);
                        }
                        FileUtil.forceDelete(new File(tmpPath));
                    }
                    catch (JV_Exception|IOException e)
                    {
                        LOG.error(e);
                    }
                }
            }
        }
        catch (JV_Exception e)
        {
            LOG.fatal(e);
            result=null;
        }
        deconnecter(sesinClient);
        return result;
    }
    
    
    
    /**
     * Cette methode recherche les documents correspondants aux rubriques fournies 
     * 
     * @param application
     * @param hParams
     * @param criteria
     * @param dependencies
     * 
     * @return les documents correspondants à la recherche
     * 
     * @throws BusinessException 
     */
    @Override
    public List<GedEntry> getDocumentsByCriterias(String serviceId, Map<String, String> hParams, GedEntry criteria) 
        throws BusinessException
    {
        Vulcain sesinClient = getSesinClient(hParams, READ_MODE);
        if (!new File(DEFAULT_SESIN_TMP_PATH).exists())
        {
            LOG.trace(new File(DEFAULT_SESIN_TMP_PATH).mkdirs());
        }
        List<GedEntry> documents = new ArrayList<>();                
        try
        {
            JV_Int iNbReponses = new JV_Int();
            JV_Int iAdrPremiereReponse = new JV_Int();
            Vector<JV_StruLOCUTION> criterions = new Vector<>();
            for (Entry<String, String> crit : criteria.getMetadatas().entrySet())
            {
                JV_StruLOCUTION criterion = new JV_StruLOCUTION(criterions.isEmpty()?JV_StruLOCUTION.RECH_MC_LIEN_OU:JV_StruLOCUTION.RECH_MC_LIEN_ET, criteria.getEntite(), crit.getKey(), '=', crit.getValue(), "", (short) 0, (short) 0);
                criterions.add(criterion);
            }
            sesinClient.PosSearchDoc(criterions, iNbReponses, iAdrPremiereReponse);
            Vector<JV_StruREP_LR> vecStruREP_LR = new Vector<>();
            sesinClient.PosGetReponsesLRD("", iAdrPremiereReponse.getValue(), iNbReponses.getValue(), iNbReponses.getValue(), 1, JV_ConsultReponse.CROISSANT, vecStruREP_LR);
            
            
            //Extraction des reponses
            for (int i = 0; i < vecStruREP_LR.size(); i++)
            {
                documents.add(getDocumentById(serviceId, hParams, Integer.toString(vecStruREP_LR.get(i).getNumDoc()), false));
            }
        }
        catch (JV_Exception e)
        {
            LOG.fatal(e);
        }
        deconnecter(sesinClient);
        return documents;
    }
    


    /**
     * Cette méthode assure la persistence d'un document
     * 
     * @param application
     * @param hParams
     * @param document
     * @param syncFileEntry
     * 
     * @return le document mergé.
     * 
     * @throws BusinessException
     */
    @Override
    public GedEntry synchronize(String serviceId, Map<String, String> hParams, GedEntry document, boolean syncFileEntry) throws BusinessException
    {
        if (!CollectionUtil.isEmpty(document.getEntries()) && document.getEntries().size()>1)
        {
            throw new BusinessException(LixboxResources.getString("MSG.ERROR.EXCEPUTI_02",new String[] {"SESPROV","document.entries"}));
        }
        if (document.getOid()!=null && !NumberUtil.isParsable(document.getOid()))
        {
            return null;
        }
        
        Vulcain sesinClient = getSesinClient(hParams, WRITE_MODE);
        if (!new File(DEFAULT_SESIN_TMP_PATH).exists())
        {
            LOG.trace(new File(DEFAULT_SESIN_TMP_PATH).mkdirs());
        }
        
        //convertion des rubriques au format sesin
        Vector<JV_StruLISTERUB> sesinRubs = new Vector<>();
        for (Entry<String, String> index : document.getMetadatas().entrySet())
        {
            sesinRubs.add(new JV_StruLISTERUB(index.getKey(), index.getValue()));
        }
        
        
        //merge de la fiche d'index
        document.setServiceId(serviceId);
        JV_StruCREATEIDX index = new JV_StruCREATEIDX((short) 0, (short) 0, "", document.getEntite(), (short) sesinRubs.size(), (JV_StruLISTERUB[]) sesinRubs.toArray(new JV_StruLISTERUB[0]));
        JV_Int badRub = new JV_Int(1);
        try
        {
            if (!StringUtil.isEmpty(document.getOid()))
            {                
                int dwNumDoc = Integer.parseInt(document.getOid());
                sesinClient.PosModifIdx(dwNumDoc, sesinRubs, badRub);
            }
            else
            {
                document.setOid(Integer.toString(sesinClient.PosCreateIdx(index, badRub)));
            }
        }
        catch (JV_Exception e)
        {
            LOG.fatal(e);
            deconnecter(sesinClient);
            throw new BusinessException(e.getMessage());
        }
        
        
        //merge des entrées
        try
        {    
            File tmp = Paths.get(DEFAULT_SESIN_TMP_PATH+"/"+document.getOid()+".bin").toFile();
            if (!tmp.exists())
            {
                LOG.debug(tmp.createNewFile());
            }            
            if (!CollectionUtil.isEmpty(document.getEntries()) && syncFileEntry)
            {
                GedFileEntry fileEntry = document.getEntries().get(0);
                fileEntry.setOid("0");
                Files.write(tmp.toPath(), fileEntry.getContent());
                FileExtension nature = FileExtension.convertFromMimeType(FileUtil.getMIMEType(tmp));
                JV_StruINFOMAJPAGE struINFOMAJPAGE = new JV_StruINFOMAJPAGE((short) 0, (short) 0, (short) 0, nature.name(), " ", fileEntry.getFileName());
                sesinClient.PosAjoutePage(tmp.getAbsolutePath(), (short) 1, Integer.parseInt(document.getOid()), (short) 1, (short) 0, struINFOMAJPAGE);
            }            
            if (tmp.exists())
            {
                FileUtil.forceDelete(tmp);
            }
            
        }
        catch (JV_Exception | Exception e)
        {
            LOG.fatal(e);
            deconnecter(sesinClient);
            throw new BusinessException(e.getMessage());
        }  
        deconnecter(sesinClient);
        return document;
    }
    
    
    
    /**
     * Cette methode assure la suppression d'un document dans la GED.
     * 
     * @param application
     * @param hParams
     * @param oid
     * 
     * @return true si la suppression est effective.
     * 
     * @throws BusinessException
     */
    @Override
    public boolean remove(String serviceId, String oid, Map<String, String> hParams) throws BusinessException
    {
        Vulcain sesinClient = getSesinClient(hParams, WRITE_MODE);
        if (!new File(DEFAULT_SESIN_TMP_PATH).exists())
        {
            LOG.trace(new File(DEFAULT_SESIN_TMP_PATH).mkdirs());
        }
        if (!NumberUtil.isParsable(oid))
        {
            return false;
        }
        boolean result = false;
        try
        {
            int dwNumDoc = Integer.parseInt(oid);
            sesinClient.PosDelDoc(dwNumDoc);
            result=true;
        }
        catch (NumberFormatException | JV_Exception e)
        {
            LOG.fatal(e);
        }
        deconnecter(sesinClient);
        return result;
    }
    

    
    /**
     * Cette methode assure l'association de deux dossiers.
     * 
     * @param hParams
     * @param oidDoc1
     * @param oidDoc2
     * 
     * @throws BusinessException 
     */
    @Override
    public boolean associerDocuments(String serviceId, Map<String, String> hParams, String oidDoc1,
            String oidDoc2) throws BusinessException
    {
        boolean result = false;
        Vulcain sesinClient = getSesinClient(hParams, READ_MODE);
        if (NumberUtil.isParsable(oidDoc1) && NumberUtil.isParsable(oidDoc2))
        {
            Vector<JV_StruINFODOSSIER> infosDos = new Vector<>();
            JV_Int nbReponses = new JV_Int(0);
            try
            {
                sesinClient.PosDosAddDossier(Integer.parseInt(oidDoc1), Integer.parseInt(oidDoc2), infosDos, nbReponses);
            }
            catch (JV_Exception|Exception e)
            {
                LOG.error(e);
                if (!e.getMessage().contains("existe deja"))
                {
                    throw new BusinessException(e.getMessage());                
                }
            }
        }
        return result;
    }
    
    
    
    private Vulcain getSesinClient(Map<String, String> hParams, int modeAuth) throws BusinessException
    {
        //recuperation de l'utilisateur sesin
        String[] userSesin = new String[2];
        switch(modeAuth)
        {
            case 2:
                userSesin[0] = hParams.get(Constant.SESIN_WRITE_LOGIN_KEY);
                userSesin[1] = hParams.get(Constant.SESIN_WRITE_PWD_KEY);
                break;
            default:
                userSesin[0] = hParams.get(Constant.SESIN_READ_LOGIN_KEY);
                userSesin[1] = hParams.get(Constant.SESIN_READ_PWD_KEY);
        }
        
        //recuperation du service entry sesin
        String sesinURI = registryClient.discoverServiceURI(hParams.get(Constant.SESIN_SERVICE_NAME_KEY), hParams.get(Constant.SESIN_SERVICE_VERSION_KEY));
        String port = (!StringUtil.isEmpty(sesinURI) && sesinURI.lastIndexOf(':')>0)?sesinURI.substring(sesinURI.lastIndexOf(':')+1):Constant.DEFAULT_SESIN_PORT;
        String host = (!StringUtil.isEmpty(sesinURI) && sesinURI.indexOf(':')==3)?sesinURI.substring(6,sesinURI.lastIndexOf(':')):Constant.DEFAULT_SESIN_HOST;
        
        //Connexion au service sesin
        Vulcain vulc = new Vulcain();
        try
        {
            JV_StruLOGIN struLogin = new JV_StruLOGIN(hParams.get(Constant.SESIN_APPLICATION_NAME_KEY), userSesin[0], userSesin[1], modeAuth);
            vulc.openSocket(host, Integer.parseInt(port));
            vulc.PosQuickLogin(struLogin);
        }
        catch (JV_Exception | Exception e)
        {
            LOG.error(e);
            deconnecter(vulc);
        }
        return vulc;
    }



    /**
     * Cette methode assure la deconnexion de la ged.
     */
    private void deconnecter(Vulcain vulcain)
    {
        try
        {
            vulcain.PosQuickLogout();
            vulcain.closeSocket();
        }
        catch (JV_Exception e)
        {
            LOG.error(e);
        }
    }
}