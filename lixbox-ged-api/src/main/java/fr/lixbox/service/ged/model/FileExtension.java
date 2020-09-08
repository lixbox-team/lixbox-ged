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

import javax.xml.bind.annotation.XmlEnum;


/**
 *  Cette classe d'enumeration regroupe toutes les
 *  extensions de fichier gérées.
 * 
 *  @author ludovic.terral
 */
@XmlEnum
public enum FileExtension
{
    //  ----------- Attribut -----------
    PNG, JPG, PDF, AVI, DOC, DOCX, TXT, XLSX, XLS, XML;

    

    // ----------- Methode -----------
    public static FileExtension convertFromMimeType(String mimeType)
    {
        switch(mimeType)
        {
            case "application/pdf":
                return FileExtension.PDF;
            case "application/xml":
                return FileExtension.XML;
            case "image/png":
                return FileExtension.PNG;
            case "image/jpeg":
                return FileExtension.JPG;
            case "video/x-msvideo":
                return FileExtension.AVI;
            case "application/msword":
                return FileExtension.DOC;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return FileExtension.DOCX;
            case "application/vnd.ms-excel":
                return FileExtension.XLS;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return FileExtension.XLSX;
            default:
                return FileExtension.TXT;
        }
    }
}