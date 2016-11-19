package com.geecommerce.catalog.product.cron;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.geecommerce.catalog.product.cron.helper.AttributeHelper;
import com.geecommerce.catalog.product.cron.helper.ProductHelper;
import com.geecommerce.catalog.product.cron.helper.VfsHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.cron.MisfireInstruction;
import com.geecommerce.core.cron.Taskable;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.annotation.Task;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import au.com.bytecode.opencsv.CSVWriter;

@DisallowConcurrentExecution
@Task(group = "CB/Catalog", name = "Product Export", schedule = "0 0/1 * * * ?", onMisfire = MisfireInstruction.RETRY_ONE, enabled = false)
public class ExportProductData implements Taskable, Job {
    @Inject
    protected App app;

    protected final VfsHelper vfsHelper;
    protected final AttributeHelper attributeHelper;
    protected final ProductHelper productHelper;

    protected List<Attribute> attributes = null;

    protected static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final String KEY_VFS_TARGET_DIR = "catalog/cron/product_export/vfs/target_dir";
    protected static final String KEY_VFS_ARCHIVE_DIR = "catalog/cron/product_export/vfs/archive_dir";
    protected static final String KEY_VFS_LOCAL_TMP_DIR = "catalog/cron/product_export/vfs/local_tmp_dir";
    protected static final String KEY_VFS_DELETE_TMP_FILE = "catalog/cron/product_export/vfs/delete_tmp_file";

    protected static final String KEY_VALUE = "value";
    protected static final String IS_DELIVERY_SCRIPT = "shipping/type/is_delivery";
    protected static final String IS_BULKYGOODS_SCRIPT = "shipping/type/is_bulky";
    protected static final String IS_PACKAGE_SCRIPT = "shipping/type/is_package";

    @Inject
    public ExportProductData(VfsHelper vfsHelper, AttributeHelper attributeHelper, ProductHelper productHelper) {
        this.vfsHelper = vfsHelper;
        this.attributeHelper = attributeHelper;
        this.productHelper = productHelper;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        Logger log = null;

        try {
            log = LogManager.getLogger(ExportProductData.class);

            this.attributes = attributeHelper.getProductAttributes();

            List<Id> productIds = productHelper.getProductIds();

            log.info("Found " + (productIds == null ? 0 : productIds.size()) + " to export.");

            String[] csvHeaderLine1 = buildHeaderLine1();
            String[] csvHeaderLine2 = buildHeaderLine2();

            log.info("csvHeaderLine1-length: " + csvHeaderLine1.length);
            log.info("csvHeaderLine2-length: " + csvHeaderLine2.length);

            log.debug("csvHeaderLine1: " + Arrays.asList(csvHeaderLine1));
            log.debug("csvHeaderLine2: " + Arrays.asList(csvHeaderLine2));

            String localTmpDir = app.cpStr_(KEY_VFS_LOCAL_TMP_DIR);

            File f = new File(localTmpDir);
            if (!f.exists())
                f.mkdirs();

            File tmpFile = new File(f, "products-export_" + fileDateFormat.format(new Date()) + ".csv");

            CSVWriter writer = null;
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;

            try {
                fos = new FileOutputStream(tmpFile);
                osw = new OutputStreamWriter(fos, "UTF-8");

                writer = new CSVWriter(osw, ';');

                // Add byte order mark (BOM) to help excel recognize the correct
                // character encoding.
                osw.write('\ufeff');

                writer.writeNext(csvHeaderLine1);
                writer.writeNext(csvHeaderLine2);

                for (Id productId : productIds) {
                    Product p = productHelper.getProduct(productId);

                    String[] csvLine = buildCsvLine(p);

                    log.trace("Writing CSV line: " + Arrays.asList(csvLine));

                    writer.writeNext(csvLine);
                    writer.flush();
                }
            } finally {
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(osw);
                IOUtils.closeQuietly(writer);
            }

            String targetDir = app.cpStr_(KEY_VFS_TARGET_DIR);
            String archiveDir = app.cpStr_(KEY_VFS_ARCHIVE_DIR);

            vfsHelper.archiveOldFiles(targetDir, archiveDir);

            vfsHelper.saveToTargetLocation(tmpFile, targetDir);

            boolean deleteTmpFile = app.cpBool_(KEY_VFS_DELETE_TMP_FILE);

            if (deleteTmpFile && tmpFile != null) {
                tmpFile.delete();
            }

            log.info("Exported " + productIds.size() + " products.");
        } catch (Throwable t) {
            // According to quartz documentation, exceptions should be caught in
            // a try-catch-block
            // wrapping the whole task. Only exceptions of type
            // JobExecutionException may be thrown.
            // http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson03#TutorialLesson3-JobExecutionException

            JobExecutionException e = new JobExecutionException(t.getMessage(), t, false);

            throw e;
        }
    }

    private String[] buildHeaderLine1() {
        ApplicationContext appCtx = app.getApplicationContext();
        List<String> languages = app.cpStrList_("catalog/cron/product_export/try_languages");

        if (languages == null | languages.isEmpty()) {
            languages.add(appCtx.getLanguage());
            languages.add("en");
            languages.add("global");
        }

        List<String> headerParts = new ArrayList<String>();
        headerParts.add("Artikelnummer");
        headerParts.add("Typ");
        headerParts.add("Verkaufbar");
        headerParts.add("Sichtbar");
        headerParts.add("Sichtbar von");
        headerParts.add("Sichtbar bis");
        headerParts.add("Url");
        headerParts.add("Versandart");

        for (Attribute attribute : attributes) {
            ContextObject<String> label = attribute.getBackendLabel();

            String labelStr = null;

            for (String lang : languages) {
                if ("global".equals(lang)) {
                    labelStr = label.getGlobalValue() == null ? null : label.getGlobalValue().toString();
                } else {
                    labelStr = label.getStr(lang);
                }

                if (!Str.isEmpty(labelStr))
                    break;
            }

            headerParts.add(labelStr);
        }

        return headerParts.toArray(new String[headerParts.size()]);
    }

    private String[] buildHeaderLine2() {
        List<String> headerParts = new ArrayList<String>();
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");
        headerParts.add("");

        for (Attribute attribute : attributes) {
            headerParts.add(attribute.getCode());
        }

        return headerParts.toArray(new String[headerParts.size()]);
    }

    private String[] buildCsvLine(Product p) {
        ApplicationContext appCtx = app.getApplicationContext();
        Store store = appCtx.getStore();
        Id storeId = store.getId();

        List<String> languages = app.cpStrList_("catalog/cron/product_export/try_languages");

        if (languages == null | languages.isEmpty()) {
            languages.add(appCtx.getLanguage());
            languages.add("en");
            languages.add("global");
        }

        List<String> csvParts = new ArrayList<String>();

        ContextObject<Boolean> saleableCtxObj = p.getSaleable();
        boolean saleable = false;
        if (saleableCtxObj != null) {
            Object saleableStoreCtx = saleableCtxObj.getValueForStore(storeId);
            Object saleableGlobal = saleableCtxObj.getGlobalValue();

            if (saleableStoreCtx != null)
                saleable = ((Boolean) saleableStoreCtx).booleanValue();

            else if (saleableGlobal != null)
                saleable = ((Boolean) saleableGlobal).booleanValue();
        }

        ContextObject<Boolean> visibleCtxObj = p.getVisible();
        boolean visible = false;
        if (visibleCtxObj != null) {
            Object visibleStoreCtx = visibleCtxObj.getValueForStore(storeId);
            Object visibleGlobal = visibleCtxObj.getGlobalValue();

            if (visibleStoreCtx != null)
                visible = ((Boolean) visibleStoreCtx).booleanValue();

            else if (visibleGlobal != null)
                visible = ((Boolean) visibleGlobal).booleanValue();
        }

        ContextObject<Date> visibleFromCtxObj = p.getVisibleFrom();
        String visibleFrom = null;
        if (visibleFromCtxObj != null) {
            Object visibleFromStoreCtx = visibleFromCtxObj.getValueForStore(storeId);
            Object visibleFromGlobal = visibleFromCtxObj.getGlobalValue();

            if (visibleFromStoreCtx != null)
                visibleFrom = dateFormat.format((Date) visibleFromStoreCtx);

            else if (visibleFromGlobal != null)
                visibleFrom = dateFormat.format((Date) visibleFromGlobal);
        }

        ContextObject<Date> visibleToCtxObj = p.getVisibleTo();
        String visibleTo = null;
        if (visibleToCtxObj != null) {
            Object visibleToStoreCtx = visibleToCtxObj.getValueForStore(storeId);
            Object visibleToGlobal = visibleToCtxObj.getGlobalValue();

            if (visibleToStoreCtx != null)
                visibleTo = dateFormat.format((Date) visibleToStoreCtx);

            else if (visibleToGlobal != null)
                visibleTo = dateFormat.format((Date) visibleToGlobal);
        }

        String uri = app.getHelper(TargetSupportHelper.class).findURI(p);

        csvParts.add(p.getArticleNumber() == null ? "" : p.getArticleNumber());
        csvParts.add(p.getType().name());
        csvParts.add(String.valueOf(saleable));
        csvParts.add(String.valueOf(visible));
        csvParts.add(visibleFrom == null ? "" : visibleFrom);
        csvParts.add(visibleTo == null ? "" : visibleTo);
        csvParts.add(uri == null ? "" : uri);
        csvParts.add(getShippingType(p));

        for (Attribute attribute : attributes) {
            AttributeValue av = p.getAttribute(attribute.getId());

            if (av != null) {
                Map<Id, AttributeOption> options = av.getAttributeOptions();

                Object value = null;

                if (options != null && options.size() > 0) {
                    Set<Id> optionIds = options.keySet();

                    StringBuilder optionsStr = new StringBuilder();

                    int x = 0;
                    for (Id optionId : optionIds) {
                        AttributeOption ao = options.get(optionId);

                        ContextObject<String> label = ao.getLabel();

                        if (label == null)
                            continue;

                        String labelStr = null;

                        for (String lang : languages) {
                            if ("global".equals(lang)) {
                                labelStr = label.getGlobalValue() == null ? null : label.getGlobalValue().toString();
                            } else {
                                labelStr = label.getStr(lang);
                            }

                            if (!Str.isEmpty(labelStr))
                                break;
                        }

                        if (x > 0)
                            optionsStr.append("|");

                        if (labelStr != null) {
                            optionsStr.append(labelStr);
                            x++;
                        }
                    }

                    value = optionsStr == null ? "" : optionsStr.toString();

                    csvParts.add(String.valueOf(value));
                } else {
                    ContextObject<?> val = av.getValue();

                    if (val != null) {
                        for (String lang : languages) {
                            if ("global".equals(lang)) {
                                value = val.getGlobalValue();
                            } else {
                                value = val.getValueFor(lang);
                            }

                            if (value != null)
                                break;
                        }

                        if (value == null)
                            value = val.getValueForStore(storeId);
                    }

                    if (value != null) {
                        if (value instanceof Number || value instanceof Boolean) {
                            // csv.append(";").append(value);
                            csvParts.add(String.valueOf(value));
                        } else if (value instanceof Date) {
                            // csv.append(";").append(dateFormat.format((Date)
                            // value));
                            csvParts.add(String.valueOf(dateFormat.format((Date) value)));
                        } else {
                            // csv.append(";\"").append(value).append("\"");
                            csvParts.add(String.valueOf(value));
                        }
                    } else {
                        csvParts.add("");
                    }
                }
            } else {
                csvParts.add("");
            }
        }

        return csvParts.toArray(new String[csvParts.size()]);
    }

    protected boolean isConditionMatched(String isConditionMatchedScriptKey, Product product) {

        String isConditionMatchedScript = app.cpStr_(isConditionMatchedScriptKey);

        if (isConditionMatchedScript != null) {
            try {
                return Groovy.conditionMatches(isConditionMatchedScript, KEY_VALUE, product);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return false;
    }

    protected String getShippingType(Product product) {
        if (isConditionMatched(IS_DELIVERY_SCRIPT, product)) {
            return "Lieferung";
        } else if (isConditionMatched(IS_BULKYGOODS_SCRIPT, product)) {
            return "Sperrgut";
        } else if (isConditionMatched(IS_PACKAGE_SCRIPT, product)) {
            return "Paket";
        }

        return "Lieferung";
    }
}
