/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.metrics;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

public class CrisMetricsDAOImpl extends AbstractHibernateDAO<CrisMetrics> implements CrisMetricsDAO {

    @Override
    public List<CrisMetrics> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
        CriteriaQuery criteriaQuery = getCriteriaQuery(criteriaBuilder, CrisMetrics.class);
        Root<CrisMetrics> crisMetricsRoot = criteriaQuery.from(CrisMetrics.class);
        criteriaQuery.select(crisMetricsRoot);

        List<javax.persistence.criteria.Order> orderList = new LinkedList<>();
        orderList.add(criteriaBuilder.asc(crisMetricsRoot.get(CrisMetrics_.id)));
        criteriaQuery.orderBy(orderList);

        return list(context, criteriaQuery, false, CrisMetrics.class, limit, offset);
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return count(createQuery(context, "SELECT count(*) from CrisMetrics"));
    }

    @Override
    public CrisMetrics uniqueLastMetricByResourceIdAndResourceTypeIdAndMetricsType(String metricType, UUID resource,
            boolean last) throws SQLException {
        return null;
    }

}