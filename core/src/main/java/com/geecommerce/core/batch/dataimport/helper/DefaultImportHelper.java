package com.geecommerce.core.batch.dataimport.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.zeroturnaround.zip.ZipUtil;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Helper
public class DefaultImportHelper implements ImportHelper {
    @Inject
    protected App app;

    protected SimpleDateFormat folderDateFormat = new SimpleDateFormat("yyyy/MM/dd_HH.mm.ss");

    @Override
    public String saveFile(InputStream in, String fileName) throws IOException {
        ApplicationContext appCtx = app.context();
        Merchant m = appCtx.getMerchant();

        String baseMerchantPath = m.getAbsoluteBaseSystemPath();

        String dateFolder = folderDateFormat.format(new Date());

        File destFolder = new File(baseMerchantPath, "data/import/" + dateFolder);

        if (!destFolder.exists())
            destFolder.mkdirs();

        File destFile = new File(destFolder.getAbsolutePath(), fileName);

        Files.copy(in, destFile.toPath());

        System.out.println("baseMerchantPath ::::::::::: " + baseMerchantPath);

        return destFile.getAbsolutePath();
    }

    @Override
    public boolean isZipFile(String filePath) {
        return filePath.toLowerCase().endsWith(".zip");
    }

    @Override
    public void unpack(String uploadedFilePath, boolean async, final Callable<?> callable) {
        if (async) {
            Runnable unpackJob = () -> {
                long start = System.currentTimeMillis();
                System.out.println("1#Starting to unzip file: " + uploadedFilePath);
                ZipUtil.explode(new File(uploadedFilePath));
                System.out.println("1#Finished unzupping unzip file: " + uploadedFilePath + " - time: " + (System.currentTimeMillis() - start));

                try {
                    callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            Thread t = new Thread(unpackJob);
            t.start();
        } else {
            long start = System.currentTimeMillis();
            System.out.println("2#Starting to unzip file: " + uploadedFilePath);
            ZipUtil.explode(new File(uploadedFilePath));
            System.out.println("2#Finished unzupping unzip file: " + uploadedFilePath + " - time: " + (System.currentTimeMillis() - start));

            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Set<String> fetchHeaders(String csvPath) throws IOException {
        File csvFilePath = new File(csvPath);

        Set<String> headers = new LinkedHashSet<>();

        if (csvFilePath.exists() && csvFilePath.canRead()) {
            if (csvFilePath.isDirectory()) {
                File[] csvFiles = csvFilePath.listFiles();

                if (csvFiles != null && csvFiles.length > 0) {
                    for (File csvFile : csvFiles) {
                        CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(Char.SEMI_COLON);
                        FileReader fileReader = new FileReader(csvFile);
                        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                        List<CSVRecord> csvRecords = csvFileParser.getRecords();
                        CSVRecord headerLine = csvRecords.get(0);
                        Iterator<String> headerNames = headerLine.iterator();

                        while (headerNames.hasNext()) {
                            String headerName = headerNames.next();

                            if (!headers.contains(headerName)) {
                                headers.add(headerName);
                            }
                        }
                    }
                }
            } else {
                CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(Char.SEMI_COLON);
                FileReader fileReader = new FileReader(csvFilePath);
                CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                List<CSVRecord> csvRecords = csvFileParser.getRecords();
                CSVRecord headerLine = csvRecords.get(0);
                Iterator<String> headerNames = headerLine.iterator();

                while (headerNames.hasNext()) {
                    String headerName = headerNames.next();

                    if (!headers.contains(headerName)) {
                        headers.add(headerName);
                    }
                }
            }
        }

        System.out.println("ALL COLUMN NAMES :::: " + headers);

        return headers;
    }

    @Override
    public void createImportPlan(String uploadedFilePath) {
        // TODO Auto-generated method stub

    }
}
