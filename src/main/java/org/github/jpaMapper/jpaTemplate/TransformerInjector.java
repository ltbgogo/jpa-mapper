package org.github.jpaMapper.jpaTemplate;

import javax.persistence.Query;

interface TransformerInjector {

    void inject(Query query);
}