package com.geecommerce.core.batch.dataimport;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.type.Id;

public interface ImportContext extends Pojo {
    ImportContext build(String token, Map<String, String> data, Model model, ImportProfile importProfile, List<ImportMessage> importMessages, String fileName, long lineNumber);

    String token();

    Map<String, String> data();

    String field(String key);

    Id idField(String key);

    Model model();

    ImportProfile importProfile();

    List<ImportMessage> importMessages();

    ImportContext add(ImportMessage importMessage);

    ImportContext property(String key, String value);

    String property(String key);

    String fileName();

    long lineNumber();
}
