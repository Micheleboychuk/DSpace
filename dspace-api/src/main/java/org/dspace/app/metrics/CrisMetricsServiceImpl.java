/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.metrics;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

public class CrisMetricsServiceImpl implements CrisMetricsService {

    @Autowired(required = true)
    protected CrisMetricsDAO crisMetricsDAO;

    @Override
    public List<CrisMetrics> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  crisMetricsDAO.findAll(context, limit, offset);
    }

    @Override
    public int count(Context context) throws SQLException {
        return crisMetricsDAO.countRows(context);
    }

    @Override
    public CrisMetrics uniqueLastMetricByResourceIdAndResourceTypeIdAndMetricsType(String metricType,
            UUID resource, boolean last) throws SQLException {
        return crisMetricsDAO.uniqueLastMetricByResourceIdAndResourceTypeIdAndMetricsType(metricType, resource, last);
    }

}
