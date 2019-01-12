package org.github.jpaMapper.metadata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class SqlMetadata {

    private String sqlKey;
    private String sqlType;
    private String sqlText;

    public static String toSqlKey(String mapperClassName, String sqlName) {
        return mapperClassName + "." + sqlName;
    }
}