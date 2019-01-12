package org.github.jpaMapper.test.book;

import com.alibaba.fastjson.JSONObject;
import org.github.jpaMapper.JpaMapper;
import org.github.jpaMapper.annotation.P;
import org.github.jpaMapper.annotation.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookMapper extends JpaMapper {

    Page<JSONObject> findAllBook(Pageable pageable);

    List<JSONObject> findAllBook();

    @Select("select * from t_book")
    List<JSONObject> findByAnno();

    int saveBook(@P("id") String id, @P("name") String name);
}
