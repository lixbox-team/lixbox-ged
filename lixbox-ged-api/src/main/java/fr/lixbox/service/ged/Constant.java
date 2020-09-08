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

public class Constant
{
    public static final String APP_CACHE_CODE = "GED";
    public static final String REDIS_NAME = "global-service-api-redis";
    public static final String REDIS_VERSION = "1.0";
    public static final String DEFAULT_REGISTRY_URL = "http://localhost:18100/registry/api/1.0";    
    public static final String SESIN_SERVICE_NAME_KEY = "SESIN_SERVICE_NAME";
    public static final String SESIN_SERVICE_VERSION_KEY = "SESIN_SERVICE_VERSION";
    public static final String SESIN_READ_LOGIN_KEY = "SESIN_READ_LOGIN";
    public static final String SESIN_WRITE_LOGIN_KEY = "SESIN_WRITE_LOGIN";
    public static final String SESIN_READ_PWD_KEY = "SESIN_READ_PWD";
    public static final String SESIN_WRITE_PWD_KEY = "SESIN_WRITE_PWD";
    public static final String SESIN_APPLICATION_NAME_KEY = "SESIN_APPLICATION_NAME";
    public static final String DEFAULT_SESIN_PORT = "8800";
    public static final String DEFAULT_SESIN_HOST = "unknow.pam.lan";
    
    public static final String PROVIDER_KEY = "GED_PROVIDER";
    
    
    private Constant()
    {
        //pas de constructeur
    }
}
