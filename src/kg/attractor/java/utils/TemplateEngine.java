package kg.attractor.java.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.StringWriter;
import java.io.IOException;
import java.util.Map;

public class TemplateEngine {
    private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

    static {
        cfg.setClassForTemplateLoading(TemplateEngine.class, "/data/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    public static String render(String templateName, Map<String, Object> data) {
        try {
            Template template = cfg.getTemplate(templateName);
            try (StringWriter writer = new StringWriter()) {
                template.process(data, writer);
                return writer.toString();
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Ошибка при обработке шаблона: " + templateName, e);
        }
    }
}