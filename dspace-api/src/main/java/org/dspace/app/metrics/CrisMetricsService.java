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

public interface CrisMetricsService {

    /**
     * Get all CrisMetrics in the whole system, paginated.
     *
     * @param context the context object
     * @param limit   limit
     * @param offset  offset
     * @return a page of CrisMetrics
     * @throws SQLException if database error
     */
    public List<CrisMetrics> findAll(Context context, Integer limit, Integer offset) throws SQLException;

    public int count(Context context) throws SQLException;

    public CrisMetrics uniqueLastMetricByResourceIdAndResourceTypeIdAndMetricsType(String metricType,
           UUID resource, boolean last) throws SQLException;
}
