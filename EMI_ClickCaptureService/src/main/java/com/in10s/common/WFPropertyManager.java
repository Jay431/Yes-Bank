/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.in10s.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 *
 * @author shivdeep.b
 */
public class WFPropertyManager {
    
   private static WFPropertyManager instance = null;

   private Map<String,JSONObject>  wfPropertyMap= null;

   private  Map<String,JSONObject>  wfComplexPropertyMap= null;
         
   public Map<String,JSONObject> getWfPropertyMap() {
        return Collections.unmodifiableMap(this.wfPropertyMap);
    }

   private Map<String,JSONObject> getWfComplexPropertyMap() {
        return Collections.unmodifiableMap(this.wfComplexPropertyMap);
    }

   private WFPropertyManager() throws Exception{
        wfPropertyMap = new HashMap<String, JSONObject>();
        wfComplexPropertyMap = new HashMap<String, JSONObject>();
        //html5_compatable_browsers_map= new HashMap<String, Float>();

        buildPropertyManagerMaps();
       //logger.debug("WfPropertyManager::"+wfPropertyMap);
        //logger.debug("wfComplexPropertyMap::"+wfComplexPropertyMap);
   }

   public static synchronized WFPropertyManager getInstance() throws Exception{
       if (instance == null)
           instance = new WFPropertyManager();
       return instance;
   }

   private void buildPropertyManagerMaps() throws Exception{
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        con = BoneCPDBPool.getConnection();
        List<String> wfPropertyGroupList = null;
        String query = "SELECT DISTINCT GROUP_NAME FROM <<SCHEMA_NAME>>.UNISERVE_APPLICATION_CONFIG";
        try{
        	
        	query = query.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
//            ps = con.prepareStatement("SELECT DISTINCT GROUP_NAME FROM YES_WF_PROPERTY_MANAGER");
        	ps = con.prepareStatement(query);
            System.out.println("conn object :::::"+con);
            rs = ps.executeQuery();
            wfPropertyGroupList = new ArrayList<String>();
            while (rs.next()) {
                wfPropertyGroupList.add(rs.getString("GROUP_NAME"));
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            
            String strQuery = "SELECT NAME,VALUE,COMPLEX_VALUE FROM <<SCHEMA_NAME>>.UNISERVE_APPLICATION_CONFIG WHERE GROUP_NAME=?";
            strQuery = strQuery.replace("<<SCHEMA_NAME>>",BoneCPDBPool.strSchemaName);
            ps = con.prepareStatement(strQuery);
            for (int i = 0; i < wfPropertyGroupList.size(); i++) {
                JSONObject groupDataJSON  = new JSONObject();
                JSONObject groupComplexDataJSON  = new JSONObject();
                ps.setString(1, wfPropertyGroupList.get(i));
                rs = ps.executeQuery();
                while (rs.next()) {
                   groupDataJSON.put(rs.getString("NAME"),rs.getString("VALUE"));
                   groupComplexDataJSON.put(rs.getString("NAME"),rs.getCharacterStream("COMPLEX_VALUE")==null?"":rs.getString("COMPLEX_VALUE"));
                }
               wfPropertyMap.put(wfPropertyGroupList.get(i), groupDataJSON);
               wfComplexPropertyMap.put(wfPropertyGroupList.get(i), groupComplexDataJSON);
            }
             if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            
        }catch(SQLException sqlCause){
            //logger.info(":: An exception in buildPropertyManagerMap method  :: " + sqlCause.getMessage());

        }finally{
           if(con!=null){
            con.close();
           }
        }   
   }

   public String getValue(String groupName, String key){
       if(groupName==null || key==null)
        return null;
      return  this.getWfPropertyMap().get(groupName).getString(key);
   }

   public Object getComplexValue(String groupName, String key){
       if(groupName==null || key==null)
        return null;
      return  this.getWfComplexPropertyMap().get(groupName).get(key);
   }
}