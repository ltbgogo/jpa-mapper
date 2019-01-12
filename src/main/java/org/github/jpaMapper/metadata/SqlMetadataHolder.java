package org.github.jpaMapper.metadata;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.github.jpaMapper.annotation.Delete;
import org.github.jpaMapper.annotation.Insert;
import org.github.jpaMapper.annotation.Select;
import org.github.jpaMapper.annotation.Update;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlMetadataHolder {

    public SqlMetadata getSqlMetadata(String sqlKey) {
        return metadataHashMap.get(sqlKey);
    }

    public Iterable<SqlMetadata> getAllSqlMetadata() {
        return metadataHashMap.values();
    }

    public void registerSqlMetadata(Class mapperClass) {
        parseClass(mapperClass);
        parseXml(mapperClass);
    }

    private void parseClass(Class mapperClass) {
        String mapperClassName = mapperClass.getName();
        for (Method method : mapperClass.getDeclaredMethods()) {
            if (method.getAnnotation(Select.class) != null) {
                putSqlMetadata(mapperClassName, method.getName(), "select", method.getAnnotation(Select.class).value()[0]);
            }
            Arrays.asList(Insert.class, Delete.class, Update.class).stream().map(e -> method.getAnnotation(e)).filter(e -> e != null).forEach(e -> {
                putSqlMetadata(mapperClassName, method.getName(), e.getClass().getSimpleName().toLowerCase(), ((String[]) AnnotationUtils.getValue(e))[0]);
            });
        }
    }

    @SneakyThrows
    private void parseXml(Class mapperClass) {
        String mapperClassName = mapperClass.getName();
        String mapperXmlPath = "/" + ClassUtils.convertClassNameToResourcePath(mapperClassName) + ".xml";
        URL mapperXmlUrl = SqlMetadata.class.getResource(mapperXmlPath);
        log.info("解析 mapper 文件: {}", mapperXmlUrl);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(false);
        Document document = docFactory.newDocumentBuilder().parse(mapperXmlUrl.toString());

        // 解析片段seg
        Map<String, String> segMap = new HashMap<>();
        iterateTags(document, "seg", e -> {
            segMap.put(e.getAttributes().getNamedItem("name").getNodeValue(), e.getTextContent());
        });
        iterateTags(document, "seg-ref", e -> {
            String ref = e.getAttributes().getNamedItem("ref").getNodeValue();
            Validate.isTrue(segMap.containsKey(ref), "seg 不存在: %s", ref);
            CDATASection seg = document.createCDATASection(segMap.get(ref));
            e.getParentNode().appendChild(seg);
            e.getParentNode().replaceChild(seg, e);
        });
        // 解析sql
        iterateTags(document, "insert,delete,update,select", e -> {
            String sqlName = e.getAttributes().getNamedItem("name").getNodeValue();
            String sqlType = e.getNodeName();
            String sqlText = e.getTextContent();
            putSqlMetadata(mapperClassName, sqlName, sqlType, sqlText);
        });
    }

    private void putSqlMetadata(String mapperClassName, String sqlName, String sqlType, String sqlText) {
        SqlMetadata metadata = new SqlMetadata(SqlMetadata.toSqlKey(mapperClassName, sqlName), sqlType, sqlText);
        String sqlKey = mapperClassName + "." + sqlName;
        Validate.isTrue(!metadataHashMap.containsKey(sqlKey), "重复的 sql: %s", sqlKey);
        metadataHashMap.put(sqlKey, metadata);
    }

    private void iterateTags(Document document, String tagNames, Consumer<Node> consumer) {
        for (String tagName : tagNames.split(",")) {
            NodeList nodeList = document.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    consumer.accept(nodeList.item(i));
                }
            }
        }
    }

    public static final Map<String, SqlMetadata> metadataHashMap = new HashMap<>();
    private final static SqlMetadataHolder instance = new SqlMetadataHolder();
    public static SqlMetadataHolder of() {
        return instance;
    }
}
