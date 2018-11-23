package com.in10s.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.in10s.listener.ApplicationListener;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class BoneCPDBPool {

    private static BoneCP connectionPool = null;
    private static String strDriveClass = "";
    public static String strSchemaName ="";
    static {

    }

    public BoneCP getConnectionPool() {

        return connectionPool;

    }

    public void setConnectionPool(BoneCP connectionPool) {

        this.connectionPool = connectionPool;

    }

    public static void init(JSONObject pros) {

        Connection connection = null;
        
        System.out.println("props : "+pros);

        try {
            
        	Class.forName(strDriveClass);

        } catch (ClassNotFoundException classEx) {

            System.out.println("EMI_ClickCaptureService - Oracle Driver Class not found Exception" + classEx.getMessage());
            classEx.printStackTrace();

        } catch (Exception ex) {

            System.out.println("EMI_ClickCaptureService - Exception while loading Oracle Driver class" + ex.getMessage());
            ex.printStackTrace();

        }

        try {

            BoneCPConfig config = new BoneCPConfig();

            config.setJdbcUrl(pros.getString("JDBC_URL"));
            config.setUsername(pros.getString("DBUSER_NAME"));
            config.setPassword((pros.containsKey("IS_PASSWORD_ENCRYPT") && !pros.getBoolean("IS_PASSWORD_ENCRYPT")) ? pros.getString("DBPASSWORD") : new CAuthenticate().Decrypt(pros.getString("DBPASSWORD")));
            config.setPartitionCount(pros.getInt("PARTION_CONT"));
            config.setConnectionTimeoutInMs(pros.getInt("CON_TIME_OUT"));
            config.setMinConnectionsPerPartition(pros.getInt("MIN_CON_PER_PARTION"));
            config.setMaxConnectionsPerPartition(pros.getInt("MAX_CON_PER_PARTION"));
            config.setLazyInit(pros.getBoolean("LAZY_INIT"));
            config.setIdleMaxAgeInMinutes(pros.getInt("MAX_AGE"));

            connectionPool = new BoneCP(config);

            connection = connectionPool.getConnection();

            if (connection != null) {

            } else {

                connectionPool = null;

            }

        } catch (SQLException e) {

            System.out.println("init Exception2 e" + e.getMessage());
            e.printStackTrace();

        } finally {

            if (connection != null) {

                try {

                    connection.close();

                } catch (SQLException e) {

                    e.printStackTrace();

                }

            }

        }

    }

    public static void initi() {

    }

    public static void shutdown() {

        try {

            connectionPool.close();

        } catch (Exception e) {

        }

        try {

            connectionPool.shutdown();

        } catch (Exception e) {

        }

    }

    public static Connection getConnection() {

        Connection con = null;

        if (connectionPool == null) {

            try {

                init(loadDbProps());

            } catch (Exception ex) {

                System.out.println("EMI_ClickCaptureService DatabaseFactory: getConnection() failed, trying again " + ex.getMessage());

            }

        }

        if (con == null) {

            try {

                con = connectionPool.getConnection();

            } catch (SQLException e) {

                System.out.println("EMI_ClickCaptureService DatabaseFactory: getConnection() failed, trying again " + e.getMessage());

            }

        }

        return con;

    }

    public static void main(String[] args) throws IOException {

        loadDbProps();

    }

    public static JSONObject loadDbProps() throws IOException {

        String strAppConfig_BONECPURL = loadAppConfigFilePath();
        Map<String, String> env = System.getenv();
        String configFilePath = env.get("APP_CONFIG_PROPERTIES");

        configFilePath = (configFilePath == null) ? System.getProperty("APP_CONFIG_PROPERTIES") : configFilePath;
        System.out.println("EMI_ClickCaptureService Env config FilePath :: " + configFilePath);

        Properties p = new Properties();
        JSONObject props = null;

        if (configFilePath == null) {

            p.load(new FileInputStream(strAppConfig_BONECPURL));
            props = (JSONObject) JSONSerializer.toJSON(p);
            
            return props;

        } else {

            p.load(new FileInputStream(configFilePath));

            props = (JSONObject) JSONSerializer.toJSON(p);
           
            return props;

        }

    }

	private static String loadAppConfigFilePath() {
		
		String strAppConfigFilePath = "";
		FileInputStream input = null;
		
		try {
			
	        Properties obj_Properties = new Properties();
	        try {
				input = new FileInputStream(ApplicationListener.strConfigurationFilePath);
				if (input != null) {

	                obj_Properties.load(input);

	                strAppConfigFilePath = obj_Properties.getProperty("BONECPURL");
	                strDriveClass = obj_Properties.getProperty("DRIVER_CLASS").trim();
	                strSchemaName = obj_Properties.getProperty("SCHEMA_NAME").trim();

	            }
			
		} catch (FileNotFoundException fnfEx) {
			
			System.out.println("EMI_ClickCaptureService : FileNotFountException occurred while fetching the Configuartion file path : "+fnfEx);
			
		} catch (IOException ioEx) {
			
			System.out.println("EMI_ClickCaptureService : IOException occurred while fetching the Configuartion file path : "+ioEx);
			
		} catch (Exception e) {
			
			System.out.println("EMI_ClickCaptureService : IOException occurred while fetching the Configuartion file path : "+e);
			
		}
		
		
		
	} catch (Exception e){
		
	}
		return strAppConfigFilePath;
	}

}
