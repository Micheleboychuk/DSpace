/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.metrics;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.dspace.content.Item;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "cris_metrics")
public class CrisMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cris_metrics_seq")
    @SequenceGenerator(name = "cris_metrics_seq", sequenceName = "cris_metrics_seq", allocationSize = 1)
    private Integer id;

    private String metricType;

    private double metricCount;

    private Date timeStampInfo;

    private Date startDate;

    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    protected Item resource;

    private boolean last;

    @Type(type = "org.hibernate.type.StringClobType")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public double getMetricCount() {
        return metricCount;
    }

    public void setMetricCount(double metricCount) {
        this.metricCount = metricCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public Date getTimeStampInfo() {
        return timeStampInfo;
    }

    public void setTimeStampInfo(Date timeStampInfo) {
        this.timeStampInfo = timeStampInfo;
    }

    public Item getResource() {
        return resource;
    }

    public void setResource(Item resource) {
        this.resource = resource;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}