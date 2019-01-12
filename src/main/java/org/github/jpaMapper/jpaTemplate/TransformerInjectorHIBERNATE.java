package org.github.jpaMapper.jpaTemplate;

import org.hibernate.transform.Transformers;

import javax.persistence.Query;

class TransformerInjectorHIBERNATE implements TransformerInjector {

    @Override
    public void inject(Query query) {
        query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    }
}
