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

import java.util.List;
import java.util.Map;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.ged.model.GedEntry;

/**
 * Cette interface définit le contrat pour qu'un provider puisse être utilisée
 * par le service
 * 
 * @author ludovic.terral
 */
public interface GedProvider
{
    // ----------- Methode(s) -----------
    /**
     * Cette methode va recuperer un document par son id.
     * 
     * @param serviceId
     * @param hParams
     * @param oid
     * @param loadContent
     * 
     * @return le document demandé
     * 
     * @throws BusinessException
     */
    GedEntry getDocumentById(String serviceId, Map<String, String> hParams, String oid, boolean loadContent)
            throws BusinessException;



    /**
     * Cette methode recherche les documents correspondants aux rubriques fournies 
     * 
     * @param serviceId
     * @param hParams
     * @param criteria
     * 
     * @return les documents correspondants à la recherche
     * 
     * @throws BusinessException 
     */
    List<GedEntry> getDocumentsByCriterias(String serviceId, Map<String, String> hParams,
            GedEntry criteria) throws BusinessException;



    /**
     * Cette méthode assure la persistence d'un document
     * 
     * @param serviceId
     * @param hParams
     * @param document
     * @param syncFileEntry
     * 
     * @return le document mergé.
     * 
     * @throws BusinessException
     */
    GedEntry synchronize(String serviceId, Map<String, String> hParams, GedEntry document,
            boolean syncFileEntry) throws BusinessException;



    /**
     * Cette methode assure la suppression d'un document dans la GED.
     * 
     * @param oid
     * 
     * @return true si la suppression est effective.
     * 
     * @throws BusinessException
     */
    boolean remove(String serviceId, String oid, Map<String, String> hParams) throws BusinessException;



    /**
     * Cette methode assure l'association de deux dossiers.
     * 
     * @param serviceId
     * @param hParams
     * @param oidDoc1
     * @param oidDoc2
     * 
     * @throws BusinessException 
     */
    boolean associerDocuments(String serviceId, Map<String, String> hParams, String oidDoc1,
            String oidDoc2) throws BusinessException;
}