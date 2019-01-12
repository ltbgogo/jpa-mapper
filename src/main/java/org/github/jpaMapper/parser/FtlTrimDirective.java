package org.github.jpaMapper.parser;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
class FtlTrimDirective implements TemplateDirectiveModel {

    private String prefix;
    private String suffix;
    private String trimPattern;

    @SneakyThrows
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        ((FtlWriter) env.getOut()).enterStack((stack, tail) -> {
            if (FtlEvel.endFlag.equals(tail)) {
                StringBuilder last = stack.pollLast();
                String str = last.toString().trim().replaceAll(trimPattern, "");
                stack.getLast().append(StringUtils.isBlank(str) ? "" : prefix + str + suffix);
            } else {
                stack.getLast().append(tail);
            }
        });
        body.render(env.getOut());
    }
}