package com.geecommerce.catalog.product.batch.dataimport;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.adapter.annotation.Adapter;
import com.geecommerce.core.batch.dataimport.ImportAdapter;
import com.geecommerce.core.batch.dataimport.model.ImportPlan;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.google.inject.Inject;

@Adapter
public class DefaultCsvImporter implements ImportAdapter {

    @Inject
    protected App app;

    @Override
    public boolean canProcess(String csvPath, ImportProfile importProfile) {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("resource")
    @Override
    public ImportPlan plan(String csvPath, ImportProfile importProfile) {
        ImportPlan importPlan = app.model(ImportPlan.class);

        try {

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

        } catch (Exception e) {
            e.printStackTrace(); // TODO
        }

        return null;

    }

    @Override
    public ImportPlan plan(byte[] bytes, ImportProfile importProfile) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public ImportPlan plan(InputStream inputStream, ImportProfile importProfile) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void process(String filePath, ImportProfile importProfile) {

    }

    @Override
    public void process(byte[] bytes, ImportProfile importProfile) {

    }

    @Override
    public void process(InputStream inputStream, ImportProfile importProfile) {

    }

}
