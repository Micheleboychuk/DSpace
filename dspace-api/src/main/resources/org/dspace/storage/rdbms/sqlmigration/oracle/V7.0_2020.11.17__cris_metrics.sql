--
-- The contents of this file are subject to the license and copyright
-- detailed in the LICENSE and NOTICE files at the root of the source
-- tree and available online at
--
-- http://www.dspace.org/license/
--

-----------------------------------------------------------------------------------
-- Create table for CrisMetrics
-----------------------------------------------------------------------------------

CREATE SEQUENCE cris_metrics_seq;

CREATE TABLE cris_metrics
(
    id INTEGER NOT NULL,
    metricType CHARACTER VARYING(255),
    metricCount FLOAT,
    timeStampInfo TIMESTAMP,
    startDate TIMESTAMP,
    endDate TIMESTAMP,
    resource_id uuid NOT NULL,
    last BOOLEAN,
	remark TYPE TEXT,
    CONSTRAINT cris_metrics_pkey PRIMARY KEY (id),
    CONSTRAINT cris_metrics_resource_id_fkey FOREIGN KEY (resource_id) REFERENCES item (uuid),
);