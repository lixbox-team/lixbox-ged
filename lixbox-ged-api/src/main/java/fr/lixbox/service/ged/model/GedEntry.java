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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.entity.model.Dao;
import fr.lixbox.orm.redis.RedisSearchValueSanitizer;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.service.ged.Constant;
import io.redisearch.Schema;

/**
 * Cette classe represente une entrée de GED.
 * 
 * @author ludovic.terral
 */
@XmlType(namespace="www.lixbox.fr/service/ged")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GedEntry extends AbstractValidatedEntity implements Dao, RedisSearchDao
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = -120171107152709L;
    
    private String oid;
    private String serviceId;
    private String entite;
    private Map<String, String> index;
    private List<GedFileEntry> entries;    
    private int occurences;
    private String highlight;
    private String synthese;
    
    private String callbackUrl;    
    
            

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
    
        
    @NotNull @NotEmpty @Size(max=70)
    public String getServiceId()
    {
        return serviceId;
    }
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }
    
    
    
    @NotNull @NotEmpty @Size(max=70)
    public String getEntite()
    {
        return entite;
    }
    public void setEntite(String entite)
    {
        this.entite = entite;
    }
    
        
    
    public Map<String, String> getIndex()
    {
        if (index == null)
        {
            index = new HashMap<>();
        }
        return index;
    }
    public void setIndex(Map<String, String> index)
    {
        this.index = index;
    }


    
    public List<GedFileEntry> getEntries()
    {
        if (entries == null)
        {
            entries = new ArrayList<>();
        }
        return entries;
    }
    public void setEntries(List<GedFileEntry> entries)
    {
        this.entries = entries;
    }

    

    public int getOccurences()
    {
        return occurences;
    }
    public void setOccurences(int occurences)
    {
        this.occurences = occurences;
    }
    
    

    public String getHighlight()
    {
        return highlight;
    }
    public void setHighlight(String highlight)
    {
        this.highlight = highlight;
    }
    
    
    
    @Size(max=256)
    public String getSynthese()
    {
        return synthese;
    }
    public void setSynthese(String synthese)
    {
        this.synthese = synthese;
    }
    
    
    
    @Size(max=256)    
    public String getCallbackUrl()
    {
        return callbackUrl;
    }
    public void setCallbackUrl(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }
    
    
    
    public static GedEntry valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<GedEntry>() {});
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public String getKey()
    {
        return Constant.APP_CACHE_CODE+":OBJECT:"+GedEntry.class.getName()+":"+oid;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema() 
    {
        Schema schema = new Schema()
                .addSortableTextField("synthese", 1)
                .addSortableTextField("serviceId",1);
        for (String index : getIndex().keySet())
        {
            schema.addSortableTextField(index, 1);
        }
        return schema;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("synthese", RedisSearchValueSanitizer.sanitizeValue(synthese));
        indexFields.put("serviceId", RedisSearchValueSanitizer.sanitizeValue(serviceId));
        for (Entry<String, String> index : getIndex().entrySet())
        {
            indexFields.put(index.getKey(), index.getValue());
        }
        return indexFields;
    }
}
