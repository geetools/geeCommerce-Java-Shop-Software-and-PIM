package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.batch.dataimport.enums.MessageLevel;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportMessage extends Model {
    Id getId();

    ImportMessage setId(Id id);

    String getToken();

    ImportMessage setToken(String token);

    String getMessage();

    ImportMessage setMessage(String message);

    MessageLevel getMessageType();

    ImportMessage setMessageType(MessageLevel messageType);

    String getFileName();

    ImportMessage setFileName(String fileName);

    Long getLineNumber();

    ImportMessage setLineNumber(Long lineNumber);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TOKEN = "token";
        public static final String MESSAGE = "msg";
        public static final String MESSAGE_LEVEL = "level";
        public static final String IMPORT_STAGE = "stage";
        public static final String FILE_NAME = "fn";
        public static final String LINE_NUMBER = "ln";
    }
}
