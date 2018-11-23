package com.in10s.applog;

import java.lang.reflect.Method;

import java.util.Properties;

public class AppLogger {

    private static Class log;
    private static Method[] myMethods;
    private static Object obj;
    private static Properties WRSLoggerConfig;
    private static String LoggerClass = "";
    private static String LoggerImplementingClass = "";
    private static String LoggerInitializationMethod = "";
    private static String Debug = "";
    private static String Info = "";
    private static String Warning = "";
    private static String Error = "";
    private static String Fatal = "";
    private static String ServerPath = "";

    private AppLogger() {

        try {

            log = Class.forName(LoggerClass);

            Method myMethod;
            String[] ArgsStrings = LoggerInitializationMethod.split("@");

            if (!ArgsStrings[1].equals("^")) {

                Class[] args = getArgs(LoggerInitializationMethod);

                myMethod = log.getMethod(ArgsStrings[0], args);

            } else {

                myMethod = log.getMethod(ArgsStrings[0]);

            }

            obj = myMethod.invoke(log);

        } catch (Exception e) {

            System.out.println("Initialization Exception : " + e);
            e.printStackTrace();

        }

    }

    private static void getInstance() {

        if (log == null) {

            try {

                LoggerClass = "org.apache.log4j.Logger";
                LoggerInitializationMethod = "getRootLogger@^";
                LoggerImplementingClass = "org.apache.log4j.spi.RootLogger";
                Debug = "debug@Object";
                Info = "info@Object";
                Warning = "warn@Object";
                Error = "error@Object@Throwable";
                Fatal = "fatal@Object@Throwable";
                new AppLogger();

            } catch (Exception e) {

                System.out.println("Exception while getting logger Object : " + e);

            }

        }

    }

    public static void debug(String strInfo) {

        try {

            getInstance();

            Class[] args = getArgs(Debug);
            Object[] params = new Object[args.length];
            String[] ArgsStrings = Debug.split("@");
            Method myMethod = log.getMethod(ArgsStrings[0], args);

            strInfo = strInfo.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator") + " :: ");
            params[0] = " :: " + strInfo;
            //System.out.println(params[0]);
            myMethod.invoke(obj, params);

        } catch (Exception e) {

            System.out.println("Exception in debug(String) :" + e);
            e.printStackTrace();

        }

    }

    public static void info(String strInfo) {

        try {

            getInstance();

            Class[] args = getArgs(Info);
            Object[] params = new Object[args.length];
            String[] ArgsStrings = Info.split("@");
            Method myMethod = log.getMethod(ArgsStrings[0], args);

            strInfo = strInfo.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator") + " :: ");
            params[0] = " :: " + strInfo;
            myMethod.invoke(obj, params);

        } catch (Exception e) {

            System.out.println("Exception in info(String) :" + e);
            e.printStackTrace();

        }

    }

    public static void warn(String strUniqueAppId, String strInfo) {

        try {

            getInstance();

            Class[] args = getArgs(Warning);
            Object[] params = new Object[args.length];
            String[] ArgsStrings = Warning.split("@");
            Method myMethod = log.getMethod(ArgsStrings[0], args);

            strInfo = strInfo.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator") + " :: " + strUniqueAppId + " :: ");
            params[0] = " :: " + strUniqueAppId + " :: " + strInfo;
            myMethod.invoke(obj, params);

        } catch (Exception e) {

            System.out.println("Exception in warn(String) :" + e);
            e.printStackTrace();

        }

    }

    public static void error(String strInfo, Throwable e1) {

        try {

            getInstance();

            Class[] args = getArgs(Error);
            Object[] params = new Object[args.length];
            String[] ArgsStrings = Error.split("@");
            Method myMethod = log.getMethod(ArgsStrings[0], args);

            strInfo = strInfo.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator") + " :: ");
            params[0] = " :: " + strInfo;
            params[1] = e1;
            myMethod.invoke(obj, params);

        } catch (Exception e) {

            System.out.println("Exception in error(String,Throwable) :" + e);
            e.printStackTrace();

        }

    }

    public static void fatal(String strUniqueAppId, String strInfo, Throwable e1) {

        try {

            getInstance();

            Class[] args = getArgs(Fatal);
            Object[] params = new Object[args.length];
            String[] ArgsStrings = Fatal.split("@");
            Method myMethod = log.getMethod(ArgsStrings[0], args);

            strInfo = strInfo.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator") + " :: " + strUniqueAppId + " :: ");
            params[0] = " :: " + strUniqueAppId + " :: " + strInfo;
            params[1] = e1;
            myMethod.invoke(obj, params);

        } catch (Exception e) {

            System.out.println("Exception in fatal(String,Throwable) :" + e);
            e.printStackTrace();

        }

    }

    public static void print() {

        try {

            getInstance();
            log = Class.forName(LoggerImplementingClass);
            myMethods = log.getMethods();

            Method[] methods = log.getMethods();

            for (int i = 0; i < methods.length; i++) {

                Object[] obj = methods[i].getParameterTypes();

                System.out.println(obj.length);

                for (int j = 0; j < obj.length; j++) {

                    System.out.println("Args : " + obj[j]);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private static Class getArgType(String strArgType) {

        Class cls = null;

        if (strArgType.equals("int")) {

            return Integer.class;

        } else if (strArgType.equals("String")) {

            return String.class;

        } else if (strArgType.equals("Exception")) {

            return new Exception().getClass();

        } else if (strArgType.equals("Throwable")) {

            return new Throwable().getClass();

        } else if (strArgType.equals("Class")) {

            return Class.class;

        } else if (strArgType.equals("Object")) {

            return Object.class;

        }

        return cls;

    }

    public static Class[] getArgs(String strArgumentsString) {

        String[] strArguments = strArgumentsString.split("@");
        Class[] args = new Class[strArguments.length - 1];

        for (int i = 0; i < (strArguments.length - 1); i++) {

            args[i] = getArgType(strArguments[i + 1]);

        }

        return args;

    }

}
