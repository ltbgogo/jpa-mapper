package org.github.jpaMapper.jpaTemplate;

import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class NamedJpaTemplate {

    private EntityManager entityManager;
    private TransformerInjector transformerInjector;

    private List<JSONObject> toCamelResultList(List<Map<String, Object>> list) {
        if (list.isEmpty()) {
            return (List) list;
        }
        String[] aliases = list.get(0).keySet().stream()
                .map(String::toLowerCase)
                .map(e -> e.split("_"))
                .map(e -> Arrays.stream(e).map(StringUtils::capitalize).reduce("", (a, b) -> a + b))
                .map(StringUtils::uncapitalize).toArray(String[]::new);
        return list.stream().map(e -> {
           JSONObject jsonObject = new JSONObject();
           Iterator iterator = e.values().iterator();
           for (int i = 0; i < aliases.length; i++) {
               jsonObject.put(aliases[i], iterator.next());
           }
           return jsonObject;
        }).collect(Collectors.toList());
    }

    public int executeUpdate(String sql, SqlParameterSource sqlParameterSource) {
        Query query = createQuery(sql, sqlParameterSource);
        return query.executeUpdate();
    }

    public Page<JSONObject> queryForPage(String sql, SqlParameterSource sqlParameterSource, Pageable pageable) {
        Query query = createQuery(QueryUtils.applySorting(sql, pageable.getSort()), sqlParameterSource);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return PageableExecutionUtils.getPage(toCamelResultList(query.getResultList()), pageable, () -> {
            String countQuerySql = String.format("select count(1) from (%s) x", sql.replaceFirst("(?iu)\\s+order\\s+by\\s+.*$", ""));
            Query countQuery = createQuery(countQuerySql, sqlParameterSource);
            return ((Number) countQuery.getSingleResult()).longValue();
        });
    }

    public List<JSONObject> queryForList(String sql, SqlParameterSource sqlParameterSource, Pageable pageable) {
        Query query = createQuery(QueryUtils.applySorting(sql, pageable.getSort()), sqlParameterSource);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return toCamelResultList(query.getResultList());
    }

    public List<JSONObject> queryForList(String sql, SqlParameterSource sqlParameterSource, Sort sort) {
        Query query = createQuery(QueryUtils.applySorting(sql, sort), sqlParameterSource);
        return toCamelResultList(query.getResultList());
    }

    public List<JSONObject> queryForList(String sql, SqlParameterSource sqlParameterSource) {
        Query query = createQuery(sql, sqlParameterSource);
        return toCamelResultList(query.getResultList());
    }

    @SneakyThrows
    private Query createQuery(String sql, SqlParameterSource sqlParameterSource) {
        Query query = entityManager.createNativeQuery(sql);
        this.transformerInjector.inject(query);
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        List<SqlParameter> sqlParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, sqlParameterSource);
        for (SqlParameter sqlParameter : sqlParameters) {
            query.setParameter(sqlParameter.getName(), sqlParameterSource.getValue(sqlParameter.getName()));
        }
        return query;
    }
}
