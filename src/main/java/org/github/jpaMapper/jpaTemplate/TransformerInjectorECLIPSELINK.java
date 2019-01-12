package org.github.jpaMapper.jpaTemplate;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;

import javax.persistence.Query;

class TransformerInjectorECLIPSELINK implements TransformerInjector {

    @Override
    public void inject(Query query) {
        query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
    }
}
