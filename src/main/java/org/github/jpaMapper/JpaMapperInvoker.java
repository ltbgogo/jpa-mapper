package org.github.jpaMapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.github.jpaMapper.annotation.P;
import org.github.jpaMapper.jpaTemplate.NamedJpaTemplate;
import org.github.jpaMapper.jpaTemplate.NamedJpaTemplateHolder;
import org.github.jpaMapper.metadata.SqlMetadata;
import org.github.jpaMapper.metadata.SqlMetadataHolder;
import org.github.jpaMapper.parser.FtlEvel;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
class JpaMapperInvoker {

    private Class<?> mapperClass;
    private String entityManagerFactoryRef;

    public Object invoke(Method method, Object[] args) {
        if (method.getName().equals("hashCode")) {
            return this.mapperClass.getName().hashCode();
        }

        NamedJpaTemplate jpaTemplate = NamedJpaTemplateHolder.get(entityManagerFactoryRef);

        AliasFor sqlNameAnnotation = method.getAnnotation(AliasFor.class);
        String sqlName = sqlNameAnnotation == null ? method.getName() : sqlNameAnnotation.value();

        // 获取sql脚本
        SqlMetadata sqlMetadata = SqlMetadataHolder.of().getSqlMetadata(SqlMetadata.toSqlKey(this.mapperClass.getName(), sqlName));
        Validate.notNull(sqlMetadata, "未找到要执行的 SQL: %s.%s", this.mapperClass.getName(), sqlName);
        String sql = sqlMetadata.getSqlText();

        // 解析sql参数
        SqlParameterSource sqlParameterSource;
        // 排除分页和排序参数
        Parameter[] parameters = Arrays.stream(method.getParameters())
                .filter(e -> e.getType() != Pageable.class && e.getType() != Sort.class)
                .toArray(Parameter[]::new);
        // 没有查询参数
        if (parameters.length == 0) {
            sql = FtlEvel.eval(sqlMetadata.getSqlKey(), Collections.EMPTY_MAP);
            sqlParameterSource = new EmptySqlParameterSource();
        } // 查询参数都放在了Map中
        else if (Map.class.isAssignableFrom(parameters[0].getType())) {
            sql = FtlEvel.eval(sqlMetadata.getSqlKey(), args[0]);
            sqlParameterSource = new MapSqlParameterSource((Map) args[0]);
        } // 查询参数是方法列表，参数名称以@P注解
        else if (parameters[0].getAnnotation(P.class) != null) {
            Map<String, Object> data = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                data.put(parameters[i].getAnnotation(P.class).value(), args[i]);
            }
            sql = FtlEvel.eval(sqlMetadata.getSqlKey(), data);
            sqlParameterSource = new MapSqlParameterSource(data);
        } // 默认查询参数到放到了自定义bean里面
        else {
            sql = FtlEvel.eval(sqlMetadata.getSqlKey(), args[0]);
            sqlParameterSource = new BeanPropertySqlParameterSource(args[0]);
        }


        // 执行sql
        if (sqlMetadata.getSqlType().equals("select")) {
            // 需要分页
            if (Arrays.stream(method.getParameterTypes()).anyMatch(e -> Pageable.class.isAssignableFrom(e))) {
                // 需要统计总记录数
                if (Page.class == method.getReturnType()) {
                    Pageable pageable = (Pageable) args[args.length - 1];
                    Page<JSONObject> page = jpaTemplate.queryForPage(sql, sqlParameterSource, pageable);
                    // 查询所有列
                    if (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0] == JSONObject.class) {
                        return page;
                    } // 查询单列
                    else {
                        List list = page.getContent().stream().map(e -> e.get(0)).collect(Collectors.toList());
                        return new PageImpl<>(list, pageable, page.getTotalElements());
                    }
                } // 只返回查询到的列表
                else if (List.class == method.getReturnType()) {
                    List<JSONObject> list = jpaTemplate.queryForList(sql, sqlParameterSource, (Pageable) args[args.length - 1]);
                    // 查询所有列
                    if (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0] == JSONObject.class) {
                        return list;
                    } // 查询单列
                    else {
                        return list.stream().map(e -> e.get(0)).collect(Collectors.toList());
                    }
                }
                throw new RuntimeException(String.format("非法的 SQL 名称：%s", method));
            } else {
                // 是否需要排序
                boolean isSort = Arrays.stream(method.getParameterTypes()).anyMatch(e -> Sort.class.isAssignableFrom(e));
                List<JSONObject> list = isSort ? jpaTemplate.queryForList(sql, sqlParameterSource, (Sort) args[args.length - 1]) : jpaTemplate.queryForList(sql, sqlParameterSource);
                // 查询列表
                if (List.class == method.getReturnType()) {
                    // 查询所有列
                    if (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0] == JSONObject.class) {
                        return list;
                    } // 查询单列
                    else {
                        return list.stream().map(e -> e.get(0)).collect(Collectors.toList());
                    }
                } // 查询单记录
                else {
                    Validate.isTrue(list.size() < 2, "查出一条以上记录！");
                    if (list.isEmpty()) {
                        return null;
                    }
                    // 查询所有列
                    if (JSONObject.class == method.getReturnType()) {
                        return list.get(0);
                    } // 查询单列
                    else {
                        return list.get(0).values().iterator().next();
                    }
                }
            }
        } else if (sqlMetadata.getSqlType().equals("update")) {
            return jpaTemplate.executeUpdate(sql, sqlParameterSource);
        }
        throw new RuntimeException("SQL 类型无法识别: " + JSON.toJSONString(jpaTemplate, false));
    }
}
