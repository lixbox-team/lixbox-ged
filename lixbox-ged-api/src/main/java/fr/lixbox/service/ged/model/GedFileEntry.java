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
package fr.lixbox.service.ged.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.stream.util.FileUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.entity.model.Dao;


/**
 * Cette classe represente un fichier d'une entrée de GED.
 * 
 * @author ludovic.terral
 */
@XmlType(namespace="www.libox.fr/service/ged")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GedFileEntry extends AbstractValidatedEntity implements Dao
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = -120171107153809L;
    
    private String oid;
    private byte[] content;
    private String mimeType;
    private String storagePath;
    private String fileName;
    
        

    // ----------- Methode(s) -----------
    @Override
    public String getOid()
    {
        return oid;
    }
    @Override
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    
    
    public byte[] getContent()
    {
        return content!=null?content.clone():null;
    }
    public void setContent(byte[] content)
    {
        this.content = content!=null?content.clone():null;
    }
    
        
    
    public String getMimeType()
    {
        if (StringUtil.isEmpty(mimeType)&&content!=null&&content.length>0)
        {
            mimeType=FileUtil.getMIMEType(content);
        }
        return mimeType;
    }
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
        
    

    @NotNull @NotEmpty @Size(max=140)
    public String getStoragePath()
    {
        return storagePath;
    }
    public void setStoragePath(String storagePath)
    {
        this.storagePath = storagePath;
    }
    
    
    
    @NotNull @NotEmpty @Size(max=140)
    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }
    
    
    
    public static GedFileEntry valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<GedFileEntry>() {});
    }
}