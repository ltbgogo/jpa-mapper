package org.github.jpaMapper.test.user;

import com.alibaba.fastjson.JSONObject;
import org.github.jpaMapper.JpaMapper;
import org.github.jpaMapper.annotation.P;

import java.util.List;

public interface UserMapper extends JpaMapper {

    List<JSONObject> findAllUser();

    int saveUser(@P("id") String id, @P("name") String name);
}
