package com.geecommerce.core.batch.dataimport.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.batch.dataimport.enums.ImportStage;
import com.geecommerce.core.batch.dataimport.enums.MessageLevel;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Model("import_messages")
public class DefaultImportMessage extends AbstractModel implements ImportMessage {
    private static final long serialVersionUID = -2155047709349897745L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.TOKEN)
    protected String token;

    @Column(Col.MESSAGE)
    protected String message = null;

    @Column(Col.MESSAGE_LEVEL)
    protected MessageLevel messageLevel = null;

    @Column(Col.IMPORT_STAGE)
    protected ImportStage importStage = null;

    @Column(Col.FILE_NAME)
    protected String fileName = null;

    @Column(Col.LINE_NUMBER)
    protected Long lineNumber = null;

    @Column(Col.ARGS)
    protected Set<String> args = null;

    @Inject
    protected App app;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ImportMessage setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public ImportMessage setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ImportMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public MessageLevel getMessageLevel() {
        return messageLevel;
    }

    @Override
    public ImportMessage setMessageLevel(MessageLevel messageLevel) {
        this.messageLevel = messageLevel;
        return this;
    }

    @Override
    public ImportStage getImportStage() {
        return importStage;
    }

    @Override
    public ImportMessage setImportStage(ImportStage importStage) {
        this.importStage = importStage;
        return this;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public ImportMessage setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public Long getLineNumber() {
        return lineNumber;
    }

    @Override
    public ImportMessage setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    @Override
    public Set<String> getArgs() {
        return args;
    }

    @Override
    public ImportMessage setArgs(Set<String> args) {
        this.args = args;
        return this;
    }

    @Override
    public ImportMessage setArgs(String... args) {
        this.args = new LinkedHashSet<String>(Arrays.asList(args));
        return this;
    }

    @Override
    public ImportMessage addArg(String arg) {
        if (Str.isEmpty(arg))
            return this;

        if (this.args == null)
            this.args = new LinkedHashSet<>();

        this.args.add(arg);

        return this;
    }

    @Override
    public String toString() {
        return "DefaultImportMessage [id=" + id + ", token=" + token + ", message=" + message + ", messageLevel=" + messageLevel + ", importStage=" + importStage + ", fileName=" + fileName
            + ", lineNumber=" + lineNumber + ", args=" + args + "]";
    }
}
