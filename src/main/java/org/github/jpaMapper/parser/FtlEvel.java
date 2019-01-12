package org.github.jpaMapper.parser;

import org.github.jpaMapper.metadata.SqlMetadata;
import org.github.jpaMapper.metadata.SqlMetadataHolder;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import lombok.SneakyThrows;

import java.io.Writer;

public class FtlEvel {

    // 结束标签
    public static final String endFlag = "=end=";
    private static final Configuration configuration;
    static {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        for (SqlMetadata sqlMetadata : SqlMetadataHolder.of().getAllSqlMetadata()) {
            String sqlTempl = sqlMetadata.getSqlText().replaceAll("(</@(select|where|orderBy)>)", "<#if true>" + endFlag + "</#if>$1");
            templateLoader.putTemplate(sqlMetadata.getSqlKey(), sqlTempl);
        }
        configuration = new Configuration(Configuration.VERSION_2_3_25);
        configuration.setTemplateLoader(templateLoader);
        // 注册变量
        VarRegister.of(configuration).register();
    }

    @SneakyThrows
    public static String eval(String sqlKey, Object context) {
        Writer writer = new FtlWriter();
        configuration.getTemplate(sqlKey).process(context, writer);
        return writer.toString();
    }
}
