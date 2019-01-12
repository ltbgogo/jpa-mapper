package org.github.jpaMapper.parser;

import freemarker.template.Configuration;
import freemarker.template.DefaultListAdapter;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;

@AllArgsConstructor(staticName = "of")
class VarRegister {

    private Configuration configuration;

    public void register() {
        // 注册标签
        this.registerTag();
        // 注册函数 - isNotBlankString
        this.registerFunctionOfIsNotBlankString();
        // 注册函数 - getInSql
        this.registerFunctionOfGetInSql();
        // 注册函数 - getIfSql
        this.registerFunctionOfGetIfSql();
        // 注册函数 - getAnyBetweenSql
        this.registerFunctionOfGetAnyBetweenSql();
    }

    /**
     * 注册函数 - getAnyBetweenSql
     */
    private void registerFunctionOfGetAnyBetweenSql() {
        configuration.setSharedVariable("getAnyBetweenSql", (TemplateMethodModelEx) args -> {
            Validate.isTrue(args.size() == 3, "getAnyBetweenSql expects 3 args!");
            return String.format("exists(select 1 from unnest(%s) t_tmp where t_tmp between %s and %s)",
                    args.get(0),
                    args.get(1),
                    args.get(2));
        });
    }

    /**
     * 注册函数 - getIfSql
     */
    private void registerFunctionOfGetIfSql() {
        configuration.setSharedVariable("getIfSql", (TemplateMethodModelEx) args -> {
            Validate.isTrue(args.size() == 3, "parseIfSql expects 3 args!");
            return String.format("case when %s then %s else %s end",
                    args.get(0),
                    args.get(1),
                    args.get(2));
        });
    }

    /**
     * 注册函数 - isNotBlankString
     */
    private void registerFunctionOfIsNotBlankString() {
        configuration.setSharedVariable("isNotBlankString", (TemplateMethodModelEx) args ->
                args.get(0) != null && ((SimpleScalar) args.get(0)).getAsString().trim().length() > 0);
    }

    /**
     * 注册函数 - getInSql
     */
    private void registerFunctionOfGetInSql() {
        configuration.setSharedVariable("getInSql", (TemplateMethodModelEx) args -> {
            String prefix = ((SimpleScalar) args.get(0)).getAsString();
            if (args.size() == 2 && args.get(1) != null) {
                DefaultListAdapter list = (DefaultListAdapter) args.get(1);
                if (list.size() == 1) {
                    return prefix + "=" + ensureValue(list.get(0));
                } else if (list.size() > 1) {
                    String values = ((List<String>) list.getWrappedObject()).stream()
                            .map(this::ensureValue)
                            .reduce((a, b) -> a + "," + b)
                            .get();
                    return String.format("%s in (%s)", prefix, values);
                }
            }
            return "";
        });
    }

    /**
     * 注册标签
     */
    private void registerTag() {
        configuration.setSharedVariable("select",
                new FtlTrimDirective(" select ", "", ",$"));
        configuration.setSharedVariable("where",
                new FtlTrimDirective(" where ", "", "\\b(and|or|where)\\b$"));
        configuration.setSharedVariable("orderBy",
                new FtlTrimDirective(" order by ", "", ",$"));
    }

    private String ensureValue(Object value) {
        Validate.notNull(value, "不支持Null！");
        if (value instanceof Number) {
            return value + "";
        } else {
            Validate.isTrue(!value.toString().contains("'"), "非法字符“'”！");
            return String.format("'%s'", value);
        }
    }
}
