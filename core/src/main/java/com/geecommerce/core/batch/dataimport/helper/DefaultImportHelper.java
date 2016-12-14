package com.geecommerce.core.batch.dataimport.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.zeroturnaround.zip.ZipUtil;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.adapter.annotation.Adapter;
import com.geecommerce.core.batch.dataimport.ImportAdapter;
import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportPlan;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.batch.exception.ImportException;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Helper
public class DefaultImportHelper implements ImportHelper {
    @Inject
    protected App app;

    protected SimpleDateFormat folderDateFormat = new SimpleDateFormat("yyyy/MM/dd_HH.mm.ss");
    protected Pattern isNumberRegex = Pattern.compile("^[0-1]+$");

    protected final RequestContexts requestContexts;

    @Inject
    public DefaultImportHelper(RequestContexts requestContexts) {
        this.requestContexts = requestContexts;
    }

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

    @SuppressWarnings("resource")
    @Override
    public Set<String> fetchHeaders(String csvPath) throws IOException {
        File csvFilePath = new File(csvPath);

        Set<String> headers = new LinkedHashSet<>();

        if (csvFilePath.exists() && csvFilePath.canRead()) {
            if (csvFilePath.isDirectory()) {
                File[] csvFiles = csvFilePath.listFiles();

                if (csvFiles != null && csvFiles.length > 0) {
                    for (File csvFile : csvFiles) {
                        CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader().withDelimiter(Char.SEMI_COLON);
                        FileReader fileReader = new FileReader(csvFile);
                        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                        Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
                        headers.addAll(headerMap.keySet());

                        // List<CSVRecord> csvRecords =
                        // csvFileParser.getRecords();
                        // CSVRecord headerLine = csvRecords.get(0);
                        // Iterator<String> headerNames = headerLine.iterator();
                        //
                        // while (headerNames.hasNext()) {
                        // String headerName = headerNames.next();
                        //
                        // if (!headers.contains(headerName)) {
                        // headers.add(headerName);
                        // }
                        // }
                    }
                }
            } else {
                CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader().withDelimiter(Char.SEMI_COLON);
                FileReader fileReader = new FileReader(csvFilePath);
                CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
                // CSVRecord headerLine = csvRecords.get(0);
                // Iterator<String> headerNames = headerLine.iterator();
                //
                // while (headerNames.hasNext()) {
                // String headerName = headerNames.next();
                //
                // if (!headers.contains(headerName)) {
                // headers.add(headerName);
                // }
                // }

                headers.addAll(headerMap.keySet());
            }
        }

        System.out.println("ALL COLUMN NAMES :::: " + headers);

        return headers;
    }

    public ImportAdapter locateImportAdapter() {
        Set<Class<?>> types = Reflect.getTypesAnnotatedWith(Adapter.class, false);

        for (Class<?> type : types) {
            if (ImportAdapter.class.isAssignableFrom(type))
                return (ImportAdapter) app.inject(type);
        }

        return null;
    }

    @Override
    public ImportPlan createImportPlan(String csvPath, ImportProfile importProfile, List<ImportMessage> importMessages) {
        ImportAdapter importAdapter = locateImportAdapter();

        System.out.println("FOUND IMPORT ADAPTER :::: " + importAdapter);

        return importAdapter.plan(csvPath, importProfile, importMessages);
    }

    @Override
    public Id toId(String value) {
        if (Str.isEmpty(value))
            return null;

        Matcher m = isNumberRegex.matcher(value);

        long num = 0;

        if (m.matches()) {
            num = Long.valueOf(value);
        }

        return num < 1 ? null : Id.valueOf(num);
    }

    @Override
    public Id merchantId(String merchant) {
        if (merchant == null)
            return null;

        Matcher matcher = isNumberRegex.matcher(merchant);
        ApplicationContext appCtx = app.context();

        Merchant m = appCtx.getMerchant();

        Id merchantId = null;

        if (matcher.matches()) {
            merchantId = Id.valueOf(merchant);
        } else {
            if (merchant.equals(m.getCode())) {
                merchantId = m.getId();
            }
        }

        if (merchantId == null || !merchantId.equals(m.getId()))
            throw new ImportException("invalidMerchant", merchant);

        return merchantId;
    }

    @Override
    public Id storeId(String store) {
        Matcher matcher = isNumberRegex.matcher(store);
        ApplicationContext appCtx = app.context();

        Merchant m = appCtx.getMerchant();
        Store s = null;
        Id storeId = null;

        if (matcher.matches()) {
            s = m.getStore(Id.valueOf(storeId));

            if (s != null)
                storeId = s.getId();
        } else {
            s = m.getStore(store);

            if (s != null)
                storeId = s.getId();
        }

        if (storeId == null)
            throw new ImportException("invalidStore", store);

        return storeId;
    }

    @Override
    public Id requestContextId(String requestContext) {
        Matcher matcher = isNumberRegex.matcher(requestContext);
        ApplicationContext appCtx = app.context();

        Merchant m = appCtx.getMerchant();
        RequestContext reqCtx = null;
        Id reqCtxId = null;

        if (matcher.matches()) {
            reqCtx = requestContexts.findById(RequestContext.class, Id.valueOf(requestContext));

            if (reqCtx != null)
                reqCtxId = reqCtx.getId();
        } else {
            reqCtx = requestContexts.forUrlPrefix(requestContext);

            if (reqCtx != null)
                reqCtxId = reqCtx.getId();
        }

        if (reqCtxId == null || !reqCtx.getMerchantId().equals(m.getId()))
            throw new ImportException("invalidRequestContext", requestContext);

        return reqCtxId;
    }

    @Override
    public boolean languageIsValid(String _language) {
        if (_language == null)
            return false;

        List<String> availableLanguages = app.cpStrList_("core/i18n/available_languages");

        if (availableLanguages == null || availableLanguages.isEmpty())
            throw new ImportException("availableLanguagesNotConfigured", _language);

        return availableLanguages.contains(_language);
    }

    @Override
    public ContextObject<?> toContextObject(Object value, Map<String, String> data) {
        return updateContextObject(new ContextObject<>(), value, data);
    }

    @Override
    public ContextObject<?> updateContextObject(ContextObject<Object> ctxObj, Object value, Map<String, String> data) {
        String _merchantId = data.get("_merchant");
        String _storeId = data.get("_store");
        String _requestContextId = data.get("_request_context");
        String _language = data.get("_language");

        if (Str.isEmpty(_merchantId) && Str.isEmpty(_storeId) && Str.isEmpty(_requestContextId) && Str.isEmpty(_language)) {
            ctxObj.addOrUpdateGlobal(value);
        } else {
            if (Str.isEmpty(_language)) {
                if (!Str.isEmpty(_merchantId)) {
                    ctxObj.addOrUpdateForMerchant(merchantId(_merchantId), value);
                }

                if (!Str.isEmpty(_storeId)) {
                    ctxObj.addOrUpdateForStore(storeId(_storeId), value);
                }

                if (!Str.isEmpty(_requestContextId)) {
                    ctxObj.addOrUpdateForRequestContext(requestContextId(_requestContextId), value);
                }

            } else {
                if (!languageIsValid(_language))
                    throw new ImportException("invalidLanguage", _language);

                if (!Str.isEmpty(_merchantId)) {
                    ctxObj.addOrUpdateForMerchant(merchantId(_merchantId), _language, value);
                }

                if (!Str.isEmpty(_storeId)) {
                    ctxObj.addOrUpdateForStore(storeId(_storeId), _language, value);
                }

                if (!Str.isEmpty(_requestContextId)) {
                    ctxObj.addOrUpdateForRequestContext(requestContextId(_requestContextId), value);
                }
            }
        }

        return ctxObj;
    }

    @Override
    public ContextObject<?> removeFromContextObject(ContextObject<Object> ctxObj, Map<String, String> data) {
        String _merchantId = data.get("_merchant");
        String _storeId = data.get("_store");
        String _requestContextId = data.get("_request_context");
        String _language = data.get("_language");

        if (Str.isEmpty(_merchantId) && Str.isEmpty(_storeId) && Str.isEmpty(_requestContextId) && Str.isEmpty(_language)) {
            ctxObj.removeEntry(ctxObj.getGlobalEntryHash());
        } else {
            if (Str.isEmpty(_language)) {
                if (!Str.isEmpty(_merchantId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForMerchant(merchantId(_merchantId)));
                }

                if (!Str.isEmpty(_storeId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForStore(storeId(_storeId)));
                }

                if (!Str.isEmpty(_requestContextId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForRequestContext(requestContextId(_requestContextId)));
                }
            } else {
                if (!Str.isEmpty(_merchantId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForMerchant(merchantId(_merchantId), _language));
                }

                if (!Str.isEmpty(_storeId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForStore(storeId(_storeId), _language));
                }

                if (!Str.isEmpty(_requestContextId)) {
                    ctxObj.removeEntry(ctxObj.getEntryHashForRequestContext(requestContextId(_requestContextId)));
                }
            }
        }

        return ctxObj;
    }
}
