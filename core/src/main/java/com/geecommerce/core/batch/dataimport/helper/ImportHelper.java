package com.geecommerce.core.batch.dataimport.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportPlan;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ImportHelper extends Helper {

    String saveFile(InputStream in, String fileName) throws IOException;

    boolean isZipFile(String filePath);

    void unpack(String uploadedFilePath, boolean async, Callable<?> callable);

    Set<String> fetchHeaders(String csvPath) throws IOException;

    ImportPlan createImportPlan(String uploadedFilePath, ImportProfile importProfile, List<ImportMessage> importMessages);

    Id toId(String value);

    Id merchantId(String merchant);

    Id storeId(String store);

    Id requestContextId(String requestContext);

    boolean languageIsValid(String _language);

    ContextObject toContextObject(Object value, Map<String, String> data);

    ContextObject updateContextObject(ContextObject<Object> ctxObj, Object value, Map<String, String> data);

    ContextObject<?> removeFromContextObject(ContextObject<Object> ctxObj, Map<String, String> data);
}
