/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.pivot4j.mdx;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.apache.log4j.Logger;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapDataSource;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.impl.PivotModelImpl;

public class MdxQueryExecutor {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(MdxQueryExecutor.class);
	
	private OlapDataSource dataSource;

	public MdxQueryExecutor(OlapDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public OlapDataSource getOlapDataSource() {
		return this.dataSource;
	}
	
	public CellSet executeMdx(String mdx) {
		logger.debug("IN: MDX = [" + mdx + "]");
		CellSet toReturn = null;
		try {
			PivotModel pivotModel = new PivotModelImpl( this.dataSource );
			pivotModel.setMdx( mdx );
			pivotModel.initialize();
			toReturn = pivotModel.getCellSet();
		} catch (Throwable t) {
			logger.error("Error while executing MDX [" + mdx + "]", t);
			throw new SpagoBIEngineRuntimeException("Error while executing MDX [" + mdx + "]", t);
		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	public Object getValueForTuple(Member[] members, Cube cube) {
		logger.debug("IN: tuple = [" + members + "]");
		Object toReturn = null;
		try {
			MDXQueryBuilder builder = new MDXQueryBuilder();
			String mdx = builder.getMDXForTuple(members, cube);
			logger.debug("Executing MDX : " + mdx + " ...");
			CellSet cellSet = this.executeMdx(mdx);
			logger.debug("MDX query executed");
			Cell cell = cellSet.getCell(0);
			toReturn = cell.getValue();
		} catch (Throwable t) {
			logger.error("Error while getting value for tuple [" + members + "]", t);
			throw new SpagoBIEngineRuntimeException("Error while getting value for tuple [" + members + "]", t);
		} finally {
			logger.debug("OUT: returning [" + toReturn + "]");
		}
		return toReturn;
	}
	
}