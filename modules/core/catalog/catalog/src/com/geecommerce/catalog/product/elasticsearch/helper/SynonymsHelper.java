package com.geecommerce.catalog.product.elasticsearch.helper;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.geecommerce.catalog.product.configuration.Key;
import com.geecommerce.core.App;
import com.geecommerce.core.config.SystemConfig;

/**
 * Created by Andrey on 29.09.2015.
 */
public class SynonymsHelper {

    private static final Logger log = LogManager.getLogger(SynonymsHelper.class);

    private static SynonymsConfiguration synonymsConfiguration;
    private static SynonymsGenerator synonymsGenerator;

    static {
        synonymsConfiguration = getConfiguration();
        synonymsGenerator = new SynonymsGenerator();
    }

    private static SynonymsConfiguration getConfiguration() {
        App app = App.get();
        SynonymsConfiguration synonymsConfiguration = new SynonymsConfiguration();
        synonymsConfiguration.setEnabled(app.cpBool_(Key.SYNONYMS_ENABLED, false));
        synonymsConfiguration.setType(app.cpEnum_(Key.SYNONYMS_GENERATION_TYPE, SynonymsConfiguration.Type.class,
            SynonymsConfiguration.Type.INLINE));
        synonymsConfiguration.setFilename(app.cpStr_(Key.SYNONYMS_PATH));
        synonymsConfiguration.setOnlyCustom(app.cpBool_(Key.SYNONYMS_ONLY_CUSTOM, true));

        return synonymsConfiguration;
    }

    public static boolean isSynonymsEnabled() {
        return synonymsConfiguration != null && synonymsConfiguration.isEnabled();
    }

    public static String createIndexSettings() {
        String result = null;
        if (isSynonymsEnabled()) {
            try {
                XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().startObject("analysis")
                    .startObject("filter").startObject("synonym").field("type", "synonym");
                if (synonymsConfiguration.getType() == SynonymsConfiguration.Type.INLINE) {
                    contentBuilder.array("synonyms", createInline().toArray());
                } else {
                    generateFile();
                    contentBuilder.field("synonyms_path", synonymsConfiguration.getFilename());
                }
                result = contentBuilder.endObject().endObject().startObject("analyzer").startObject("synonym")
                    .field("tokenizer", "whitespace").array("filter", "synonym", "lowercase").endObject()
                    .endObject().endObject().endObject().string();
            } catch (IOException e) {
                log.info("ERROR CREATING SYNONYMS CONFIGURATION!");
                throw new RuntimeException(e);
            }
        } else {
            log.info("SYNONYMS CONFIGURATION IS NOT FOUND!");
        }
        return result;
    }

    private static String createRelativeFilePath() {
        return SystemConfig.GET.val(SystemConfig.ELASTICSEARCH_PATH) + "config/" + synonymsConfiguration.getFilename();
    }

    public static void generateFile() {
        if (isSynonymsEnabled()) {
            synonymsGenerator.generateFile(createRelativeFilePath());
        } else {
            log.info("SYNONYMS CONFIGURATION IS NOT FOUND!");
        }
    }

    private static List<String> createInline() {
        return synonymsGenerator.generateInlineSynonymConfiguration(synonymsConfiguration);
    }

    static class SynonymsConfiguration {
        public enum Type {
            FILE, INLINE
        };

        private String filename;
        private Boolean enabled;
        private Boolean onlyCustom;
        private Type type;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Boolean isEnabled() {
            return enabled != null && enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public Boolean getOnlyCustom() {
            return onlyCustom;
        }

        public void setOnlyCustom(Boolean onlyCustom) {
            this.onlyCustom = onlyCustom;
        }
    }
}
