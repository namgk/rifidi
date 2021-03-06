/*
 * 
 * RifidiReportFactoryimpl.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECFilterListMember;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportOutputFieldSpec;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ECReportOptions;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;
import org.rifidi.edge.epcglobal.aleread.filters.EPCTAGPatternMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.FilterFactory;
import org.rifidi.edge.epcglobal.aleread.filters.PatternMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.ReportALEField;
import org.rifidi.edge.epcglobal.aleread.groups.GroupFactory;
import org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;

/**
 * Factory for creating reports from ECReportSpecs
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiReportFactoryimpl implements RifidiReportFactory {
	/** Factory for creating filter instances. */
	private FilterFactory filterFactory;
	/** Factory for creating grouping rules */
	private GroupFactory groupFactory;

	/**
	 * Constructor.
	 */
	public RifidiReportFactoryimpl() {
		filterFactory = new FilterFactory();
		groupFactory = new GroupFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.service.RifidiReportFactory#createReport
	 * (org.rifidi.edge.epcglobal.ale.api.read.data.ECReportSpec)
	 */
	public RifidiReport createReport(ECReportSpec reportSpec)
			throws ECSpecValidationExceptionResponse {
		int options = 0;
		if ("ADDITIONS".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_ADDITIONS;
		} else if ("DELETIONS".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_DELETIONS;
		} else if ("CURRENT".equals(reportSpec.getReportSet().getSet())) {
			options |= ECReportOptions.REPORT_CURRENT;
		} else {
			throw new ECSpecValidationExceptionResponse(
					"Unknown report set type "
							+ reportSpec.getReportSet().getSet());
		}
		Map<ALEField, List<PatternMatcher>> includeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		Map<ALEField, List<PatternMatcher>> excludeFilters = new HashMap<ALEField, List<PatternMatcher>>();
		if (reportSpec.getFilterSpec() != null) {
			// ALE 1.1
			if (reportSpec.getFilterSpec().getExtension() != null) {
				for (ECFilterListMember filter : reportSpec.getFilterSpec()
						.getExtension().getFilterList().getFilter()) {
					if ("INCLUDE".equals(filter.getIncludeExclude())) {
						includeFilters.putAll(filterFactory
								.createMatcher(filter));
					} else if ("EXCLUDE".equals(filter.getIncludeExclude())) {
						excludeFilters.putAll(filterFactory
								.createMatcher(filter));
					} else {
						throw new ECSpecValidationExceptionResponse(
								"Unknown filter type "
										+ filter.getIncludeExclude());
					}
				}
			}

			// ALE 1.0
			ECFieldSpec spec = new ECFieldSpec();
			spec.setDatatype("epc");
			spec.setFieldname("epc");
			spec.setFormat("epc-tag");
			ALEField field = new ALEField(spec);
			// TODO: kinda crappy, reimplement
			if (reportSpec.getFilterSpec().getExcludePatterns() != null) {
				for (String pattern : reportSpec.getFilterSpec()
						.getExcludePatterns().getExcludePattern()) {
					if (!pattern.equals("")) {
						if (EPCTAGPatternMatcher.isValidPattern(pattern)) {
							if (excludeFilters.get(field) == null) {
								excludeFilters.put(field,
										new ArrayList<PatternMatcher>());
							}
							excludeFilters.get(field).add(
									new EPCTAGPatternMatcher(pattern));
						}
					}
				}
			}
			if (reportSpec.getFilterSpec().getIncludePatterns() != null) {
				for (String pattern : reportSpec.getFilterSpec()
						.getIncludePatterns().getIncludePattern()) {
					if (!pattern.equals("")) {
						if (EPCTAGPatternMatcher.isValidPattern(pattern)) {
							if (includeFilters.get(field) == null) {
								includeFilters.put(field,
										new ArrayList<PatternMatcher>());
							}
							includeFilters.get(field).add(
									new EPCTAGPatternMatcher(pattern));
						}
					}
				}
			}
		}

		List<GroupMatcher> groups = new ArrayList<GroupMatcher>();
		ALEField groupfield = null;
		if (reportSpec.getGroupSpec() != null) {
			// ALE 1.1
			if (reportSpec.getGroupSpec().getExtension() != null) {
				if (reportSpec.getGroupSpec().getExtension().getFieldspec()
						.getFieldname() != null) {
					groupfield = new ALEField(reportSpec.getGroupSpec()
							.getExtension().getFieldspec());
				}
			}
			// ALE 1.0
			if (groupfield == null) {
				ECFieldSpec spec = new ECFieldSpec();
				spec.setDatatype("epc");
				spec.setFieldname("epc");
				spec.setFormat("epc-tag");
				groupfield = new ALEField(spec);
			}
			for (String pattern : reportSpec.getGroupSpec().getPattern()) {
				if (!pattern.equals("")) {
					groups.add(groupFactory.createMatcher(groupfield, pattern));
				}
			}
		}
		// collect the different options
		if (reportSpec.getOutput().isIncludeCount()) {
			options = options | ECReportOptions.INCLUDE_COUNT;
		}
		if (reportSpec.getOutput().isIncludeEPC()) {
			options = options | ECReportOptions.INCLUDE_EPC;
		}
		if (reportSpec.getOutput().isIncludeRawDecimal()) {
			options = options | ECReportOptions.INCLUDE_RAW_DECIMAL;
		}
		if (reportSpec.getOutput().isIncludeRawHex()) {
			options = options | ECReportOptions.INCLUDE_RAW_HEX;
		}
		if (reportSpec.getOutput().isIncludeTag()) {
			options = options | ECReportOptions.INCLUDE_TAG;
		}
		if (reportSpec.isReportIfEmpty()) {
			options = options | ECReportOptions.REPORT_IF_EMPTY;
		}
		if (reportSpec.isReportOnlyOnChange()) {
			options = options | ECReportOptions.REPORT_ONLY_ON_CHANGE;
		}
		// collect the fields for the report
		Set<ReportALEField> reportFields = new HashSet<ReportALEField>();
		if (reportSpec.getOutput().getExtension() != null
				&& reportSpec.getOutput().getExtension().getFieldList() != null) {
			for (ECReportOutputFieldSpec spec : reportSpec.getOutput()
					.getExtension().getFieldList().getField()) {
				reportFields.add(new ReportALEField(spec.getName(), spec
						.getFieldspec()));
			}
		}
		// check if we are actually gettin an output.
		if (reportFields.size() == 0
				&& (options & (ECReportOptions.INCLUDE_RAW_DECIMAL
						| ECReportOptions.INCLUDE_RAW_HEX
						| ECReportOptions.INCLUDE_TAG | ECReportOptions.INCLUDE_EPC)) == 0) {
			throw new ECSpecValidationExceptionResponse("No output specified. ");
		}
		return new RifidiReport(reportSpec.getReportName(), options,
				includeFilters, excludeFilters, groupfield, groups,
				reportFields);
	}
}
