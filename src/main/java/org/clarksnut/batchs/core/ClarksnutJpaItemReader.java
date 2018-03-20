package org.clarksnut.batchs.core;

import org.jberet.support.io.JpaItemReader;

import javax.inject.Named;
import javax.persistence.Query;
import java.util.Map;

@Named
public class ClarksnutJpaItemReader extends JpaItemReader {

    @Override
    protected Query getQuery() {
        Query q = null;
        if (queryInstance != null && !queryInstance.isUnsatisfied()) {
            q = queryInstance.get();
        }
        if (q == null) {
            if (jpqlQuery != null) {
                q = beanType != null ? em.createQuery(jpqlQuery, beanType) :
                        em.createQuery(jpqlQuery);
            } else if (namedQuery != null) {
                q = beanType != null ? em.createNamedQuery(namedQuery, beanType) :
                        em.createNamedQuery(namedQuery);
            } else if (nativeQuery != null) {
                q = beanType != null ? em.createNativeQuery(nativeQuery, beanType) :
                        resultSetMapping != null ? em.createNativeQuery(nativeQuery, resultSetMapping) :
                                em.createNativeQuery(nativeQuery);
            } else if (storedProcedureQuery != null) {
                q = beanType != null ? em.createStoredProcedureQuery(storedProcedureQuery, beanType) :
                        resultSetMapping != null ? em.createStoredProcedureQuery(storedProcedureQuery, resultSetMapping) :
                                em.createStoredProcedureQuery(storedProcedureQuery);
            } else if (namedStoredProcedureQuery != null) {
                q = em.createNamedStoredProcedureQuery(namedStoredProcedureQuery);
            }
        }

        if (firstResult != 0) {
            q.setFirstResult(firstResult);
        }
        if (maxResults != 0) {
            q.setMaxResults(maxResults);
        }

        if (hints != null) {
            for (final Map.Entry<String, String> e : hints.entrySet()) {
                if (e.getKey().equals("javax.persistence.fetchgraph") || e.getKey().equals("javax.persistence.loadgraph")) {
                    q.setHint(e.getKey(), em.getEntityGraph(e.getValue()));
                } else {
                    q.setHint(e.getKey(), e.getValue());
                }
            }
        }

        return q;
    }
}
