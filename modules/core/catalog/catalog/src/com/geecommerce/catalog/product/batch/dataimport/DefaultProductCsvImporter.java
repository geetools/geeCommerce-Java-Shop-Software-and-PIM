package com.geecommerce.catalog.product.batch.dataimport;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.geecommerce.catalog.product.batch.dataimport.helper.ProductImportHelper;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.adapter.annotation.Adapter;
import com.geecommerce.core.batch.dataimport.ImportAdapter;
import com.geecommerce.core.batch.dataimport.enums.ImportStage;
import com.geecommerce.core.batch.dataimport.enums.MessageLevel;
import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportPlan;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Adapter
public class DefaultProductCsvImporter implements ImportAdapter {

    @Inject
    protected App app;

    protected final ProductImportHelper productImportHelper;
    protected final Products products;

    protected SimpleDateFormat collectionDateFormat = new SimpleDateFormat("yyMMddHHmmss");

    @Inject
    public DefaultProductCsvImporter(ProductImportHelper productImportHelper, Products products) {
        this.productImportHelper = productImportHelper;
        this.products = products;
    }

    @Override
    public boolean canProcess(String csvPath, ImportProfile importProfile) {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("resource")
    @Override
    public ImportPlan plan(String csvPath, ImportProfile importProfile, List<ImportMessage> importMessages) {

        long start = System.currentTimeMillis();

        String collectionName = "product_ids_" + collectionDateFormat.format(new Date());

        products.buildTmpProductIdsCollection(collectionName);

        //
        // System.out.println("FOUND PRODUCT BY IDS1?????????????????????? " +
        // p);
        //
        // p = products.findProductByIds(collectionName, "12345", "12345.01",
        // null);
        //
        // System.out.println("FOUND PRODUCT BY IDS2?????????????????????? " +
        // p);
        //
        // p = products.findProductByIds(collectionName, "12345", "12345.01",
        // 1234567890L);
        //
        // System.out.println("FOUND PRODUCT BY IDS3?????????????????????? " +
        // p);

        Set<String> productsToCreate = new LinkedHashSet<>();
        Set<Id> productsToUpdate = new LinkedHashSet<>();
        Set<Id> productsToDelete = new LinkedHashSet<>();
        Set<Id> updateDeleteCollions = new LinkedHashSet<>();

        ImportPlan importPlan = app.model(ImportPlan.class);

        try {
            File csvFilePath = new File(csvPath);

            if (csvFilePath.exists() && csvFilePath.canRead()) {
                if (csvFilePath.isDirectory()) {
                    File[] csvFiles = csvFilePath.listFiles();

                    if (csvFiles != null && csvFiles.length > 0) {
                        for (File csvFile : csvFiles) {
                            CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader().withDelimiter(Char.SEMI_COLON);
                            FileReader fileReader = new FileReader(csvFile);
                            CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                            List<CSVRecord> csvRecords = csvFileParser.getRecords();

                            buildPlan(csvFile, importProfile, importPlan,
                                csvRecords, importMessages, collectionName,
                                productsToCreate, productsToUpdate, productsToDelete, updateDeleteCollions);

                        }
                    }
                } else {
                    CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader().withDelimiter(Char.SEMI_COLON);
                    FileReader fileReader = new FileReader(csvFilePath);
                    CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    buildPlan(csvFilePath, importProfile, importPlan,
                        csvRecords, importMessages, collectionName,
                        productsToCreate, productsToUpdate, productsToDelete, updateDeleteCollions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // TODO
        } finally {
            try {
                products.dropTmpProductIdsCollection(collectionName);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        System.out.println("::::: productsToUpdate ::::: " + productsToUpdate);
        System.out.println("::::: productsToDelete ::::: " + productsToDelete);

        if (!updateDeleteCollions.isEmpty()) {
            productsToUpdate.removeAll(updateDeleteCollions);
            productsToDelete.removeAll(updateDeleteCollions);
        }

        importPlan.addAction("addingProducts", productsToCreate.size());
        importPlan.addAction("updatingProducts", productsToUpdate.size());
        importPlan.addAction("deletingProducts", productsToDelete.size());

        System.out.println("Time taken for building import plan ::::: " + (System.currentTimeMillis() - start) + "ms");

        return importPlan;
    }

    protected void buildPlan(File csvFilePath, ImportProfile importProfile, ImportPlan importPlan,
        List<CSVRecord> csvRecords, List<ImportMessage> importMessages, String collectionName,
        Set<String> productsToCreate, Set<Id> productsToUpdate, Set<Id> productsToDelete, Set<Id> updateDeleteCollions) {
        for (CSVRecord csvRecord : csvRecords) {
            if (productImportHelper.isEmpty(csvRecord)) {
                continue;
            }

            importPlan.addAction("processTotalRows");

            if (!productImportHelper.isValid(csvRecord)) {
                importMessages.add(app.model(ImportMessage.class)
                    .setToken(importProfile.getToken())
                    .setFileName(csvFilePath.getName())
                    .setLineNumber(csvRecord.getRecordNumber())
                    .setMessageLevel(MessageLevel.ERROR)
                    .setImportStage(ImportStage.PLAN)
                    .setMessage(productImportHelper.errorMessage(csvRecord)));
                // Ignore invalid CSV rows.
                continue;
            }

            Id currentProductId = null;

            if (csvRecord.isSet("_id") && !Str.isEmpty(csvRecord.get("_id"))) {
                currentProductId = Id.valueOf(csvRecord.get("_id"));

                if (!products.contains(collectionName, currentProductId)) {
                    importMessages.add(app.model(ImportMessage.class)
                        .setToken(importProfile.getToken())
                        .setFileName(csvFilePath.getName())
                        .setLineNumber(csvRecord.getRecordNumber())
                        .setMessageLevel(MessageLevel.ERROR)
                        .setImportStage(ImportStage.PLAN)
                        .setMessage("productIdNotFound")
                        .addArg(csvRecord.get("_id")));
                    // Ignore invalid CSV rows.
                    continue;
                }
            } else {
                if ((csvRecord.isSet("id2") && !Str.isEmpty(csvRecord.get("id2")))
                    || (csvRecord.isSet("ean") && !Str.isEmpty(csvRecord.get("ean")))
                    || (csvRecord.isSet("article_number") && !Str.isEmpty(csvRecord.get("article_number")))) {

                    boolean id2Exists = (csvRecord.isSet("id2") && !Str.isEmpty(csvRecord.get("id2")));
                    boolean eanExists = (csvRecord.isSet("ean") && !Str.isEmpty(csvRecord.get("ean")));
                    boolean articleNumberExists = (csvRecord.isSet("article_number") && !Str.isEmpty(csvRecord.get("article_number")));

                    Map<String, Object> idsMap = products.productIds(collectionName,
                        id2Exists ? csvRecord.get("id2") : null,
                        articleNumberExists ? csvRecord.get("article_number") : null,
                        eanExists ? Long.valueOf(csvRecord.get("ean")) : null);

                    if (idsMap != null && idsMap.containsKey("_id")) {
                        System.out.println("[PLAN] UPDATING PRODUCT FOR ID MAP: " + idsMap);

                        currentProductId = Id.valueOf(idsMap.get("_id"));
                    } else {
                        // Must be a new product.
                        System.out.println("[PLAN] CREATING PRODUCT FOR ID MAP: " + idsMap);
                    }
                }
            }

            if (productImportHelper.variantExists(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removeVariants");

                } else {
                    importPlan.addAction("addVariants");
                }
            } else if (productImportHelper.programmeProductExists(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removeProgrammeProducts");
                } else {
                    importPlan.addAction("addProgrammeProducts");
                }
            } else if (productImportHelper.bundleProductExists(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removeBundleProducts");
                } else {
                    importPlan.addAction("addBundleProducts");
                }
            } else if (productImportHelper.upsellProductExists(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removeUpsellProducts");
                } else {
                    importPlan.addAction("addUpsellProducts");
                }
            } else if (productImportHelper.crossSellProductExists(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removeCrossSellProducts");
                } else {
                    importPlan.addAction("addCrossSellProducts");
                }
            }

            if (productImportHelper.hasMedia(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importMessages.add(app.model(ImportMessage.class)
                        .setToken(importProfile.getToken())
                        .setFileName(csvFilePath.getName())
                        .setLineNumber(csvRecord.getRecordNumber())
                        .setMessageLevel(MessageLevel.ERROR)
                        .setImportStage(ImportStage.PLAN)
                        .setMessage("deletingMediaNotPossible")
                        .addArg(csvRecord.get("_media")));
                    // Ignore invalid CSV rows.
                    continue;
                } else {
                    importPlan.addAction("addMedia");
                }
            }

            if (productImportHelper.hasPrice(csvRecord)) {
                if (productImportHelper.isDeleteAction(csvRecord)) {
                    importPlan.addAction("removePrices");
                } else {
                    importPlan.addAction("addPrices");
                }
            }

            if (productImportHelper.hasQuantity(csvRecord)) {
                importPlan.addAction("updateQuantity");
            }

            if (currentProductId != null && productImportHelper.isDeleteAction(csvRecord)) {
                if (productsToUpdate.contains(currentProductId)) {
                    if (!updateDeleteCollions.contains(currentProductId)) {
                        ImportMessage msg = app.model(ImportMessage.class)
                            .setToken(importProfile.getToken())
                            .setFileName(csvFilePath.getName())
                            .setLineNumber(csvRecord.getRecordNumber())
                            .setMessageLevel(MessageLevel.ERROR)
                            .setImportStage(ImportStage.PLAN)
                            .setMessage("updateDeleteCollision")
                            .addArg(currentProductId.str());

                        String productKeys = productImportHelper.productKeys(csvRecord);

                        if (!Str.isEmpty(productKeys))
                            msg.addArg(productKeys);

                        importMessages.add(msg);
                        updateDeleteCollions.add(currentProductId);
                    }

                    continue;
                }

                productsToDelete.add(currentProductId);
            } else if (currentProductId != null
                && (productImportHelper.isUpdateAction(csvRecord)
                    || (!productImportHelper.isNewAction(csvRecord)
                        && !productImportHelper.isDeleteAction(csvRecord)
                        && productImportHelper.containsUpdatableFields(csvRecord)))) {
                if (productsToDelete.contains(currentProductId)) {
                    if (!updateDeleteCollions.contains(currentProductId)) {
                        ImportMessage msg = app.model(ImportMessage.class)
                            .setToken(importProfile.getToken())
                            .setFileName(csvFilePath.getName())
                            .setLineNumber(csvRecord.getRecordNumber())
                            .setMessageLevel(MessageLevel.ERROR)
                            .setImportStage(ImportStage.PLAN)
                            .setMessage("updateDeleteCollision")
                            .addArg(currentProductId.str());

                        String productKeys = productImportHelper.productKeys(csvRecord);

                        if (!Str.isEmpty(productKeys))
                            msg.addArg(productKeys);

                        importMessages.add(msg);
                        updateDeleteCollions.add(currentProductId);
                    }

                    continue;
                }

                productsToUpdate.add(currentProductId);
            } else if ((currentProductId == null || productImportHelper.isNewAction(csvRecord)) && productImportHelper.containsUpdatableFields(csvRecord)) {
                if (productImportHelper.containsMinimalFieldsForNewProduct(csvRecord)) {
                    productsToCreate.add(productImportHelper.newProductKey(csvRecord));
                } else {
                    ImportMessage msg = app.model(ImportMessage.class)
                        .setToken(importProfile.getToken())
                        .setFileName(csvFilePath.getName())
                        .setLineNumber(csvRecord.getRecordNumber())
                        .setMessageLevel(MessageLevel.ERROR)
                        .setImportStage(ImportStage.PLAN)
                        .setMessage("incompleteNewProduct");

                    String productKeys = productImportHelper.productKeys(csvRecord);

                    if (!Str.isEmpty(productKeys))
                        msg.addArg(productKeys);

                    importMessages.add(msg);
                }
            }
        }

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
