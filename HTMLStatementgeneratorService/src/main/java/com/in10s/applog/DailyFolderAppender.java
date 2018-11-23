package com.in10s.applog;

import org.apache.commons.io.FileUtils;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Random;

public final class DailyFolderAppender extends DailyRollingFileAppender {

    private static String DATE_PATTERN = "yyyy-MM-dd";
    static String logFileName = "";
    private static int maxIndex = 0;
    private long reqnum = 1;
    private String mstrRootFolder;
    private String mstrDateFolder;
    private long reqNum = generateRequestNumber(5);
    private String mstrServerFolder = String.valueOf(reqNum);
    private String mstrDatePattern = DATE_PATTERN;
    private String mstrFileName;
    private SimpleDateFormat mobjSDF;
    private String maxfile;

    public DailyFolderAppender() {

    }

    synchronized long generateRequestNumber(int length) {

        Random random = new Random();
        char[] digits = new char[length];

        digits[0] = (char) (random.nextInt(9) + '1');

        for (int i = 1; i < length; i++) {

            digits[i] = (char) (random.nextInt(10) + '0');

        }

        return Long.parseLong(new String(digits));

    }

    @Override
    public void activateOptions() {

        maxIndex = 0;

        if (getMaxFileSize() == null) {

            setMaxFileSize(String.valueOf(250 * 1024 * 1024));

        }

        logFileName = mstrFileName.substring(0, mstrFileName.indexOf("."));
        createFolder(this.mstrRootFolder + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        fileName = this.mstrRootFolder + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) + "/" + logFileName + "_" + this.mstrServerFolder + "_I" + maxIndex + "_" + this.mstrDateFolder + mstrFileName.substring(mstrFileName.lastIndexOf("."), mstrFileName.length());
        logFileName = mstrFileName;

        super.fileName = this.fileName;
        super.activateOptions();

    }

    public String getMaxFileSize() {

        return this.maxfile;

    }

    public void setMaxFileSize(String MaxFileSize) {

        try {

            Long.parseLong(MaxFileSize);

        } catch (Exception e) {

            MaxFileSize = String.valueOf(250 * 1024 * 1024);

        }

        this.maxfile = MaxFileSize;

    }

    @Override
    public String getDatePattern() {

        return this.mstrDatePattern;

    }

    public String getRootFolder() {

        return this.mstrRootFolder;

    }

    public String getFileName() {

        return this.mstrFileName;

    }

    @Override
    protected void setQWForFiles(Writer writer) {

        qw = new CountingQuietWriter(writer, errorHandler);

    }

    @Override
    public void setDatePattern(String pstrPattern) {

        this.mstrDatePattern = checkPattern(pstrPattern);
        this.mstrDateFolder = this.mobjSDF.format(new Date(System.currentTimeMillis()));

    }

    public void setRootFolder(String pstrFolder) {

        this.mstrRootFolder = createFolder(pstrFolder);

    }

    public void setFileName(String pstrFile) {

        this.mstrFileName = pstrFile;

    }

    @Override
    protected void subAppend(LoggingEvent pobjEvent) {

        Date dtNow;
        String strFolder;

        dtNow = new Date(System.currentTimeMillis());
        strFolder = this.mobjSDF.format(dtNow);

        if (!strFolder.equals(this.mstrDateFolder)) {

            try {

                rollOver(strFolder);
                this.mstrDateFolder = strFolder;

            } catch (IOException IOEx) {

            }

        } else if ((fileName != null) && (((CountingQuietWriter) qw).getCount() >= Long.valueOf(getMaxFileSize()))) {

            try {

                rollBySize(strFolder);

            } catch (IOException IOEx) {

            }

        }

        try {

            super.subAppend(pobjEvent);

        } catch (Exception e) {

        }

    }

    private String checkPattern(String pstrPattern) {

        String strRet = null;
        SimpleDateFormat objFmt = new SimpleDateFormat(DATE_PATTERN);

        try {

            this.mobjSDF = new SimpleDateFormat(pstrPattern);
            strRet = pstrPattern;

        } catch (NullPointerException NPExIgnore) {

            this.mobjSDF = objFmt;
            strRet = DATE_PATTERN;

        } catch (IllegalArgumentException IlArgExIgnore) {

            this.mobjSDF = objFmt;
            strRet = DATE_PATTERN;

        } finally {

            objFmt = null;

        }

        return strRet;

    }

    private String createFolder(String pstrPath) {

        if ((pstrPath != null) && (pstrPath.length() > 2)) {

            try {

                FileUtils.forceMkdir(new File(pstrPath));

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return pstrPath;

    }

    private void rollOver(String pstrName) throws IOException {

        if (pstrName.equals(this.mstrDateFolder)) {

            return;

        }

        maxIndex = 0;
        makeRollOver(pstrName);

    }

    private void rollBySize(String pstrName) throws IOException {

        maxIndex = maxIndex + 1;
        makeRollOver(pstrName);

    }

    private void makeRollOver(String pstrName) throws IOException {

        String strTemp;

        this.closeFile();
        this.mstrDateFolder = pstrName;
        logFileName = mstrFileName.substring(0, mstrFileName.indexOf("."));
        createFolder(this.mstrRootFolder + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        strTemp = this.mstrRootFolder + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) + "/" + logFileName + "_" + this.mstrServerFolder + "_I" + maxIndex + "_" + this.mstrDateFolder + mstrFileName.substring(mstrFileName.lastIndexOf("."), mstrFileName.length());
        this.fileName = strTemp;
        super.fileName = this.fileName;
        logFileName = mstrFileName;
        this.setFile(strTemp, false, this.bufferedIO, this.bufferSize);
        ((CountingQuietWriter) qw).setCount(0);

    }

    public static void main(String[] args) {

        System.out.println("::: " + new SimpleDateFormat(DATE_PATTERN).format(new Date(System.currentTimeMillis())));

    }

}
