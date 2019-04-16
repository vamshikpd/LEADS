package com.endeca.ui;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.DimensionSearchResult;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.DimensionSearchResultGroupList;
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEException;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.FieldList;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.ui.ConsolidatedQueryResult;
import com.endeca.ui.ParamHandler;
import com.plm.util.PLMUtil;
import com.util.http.QueryHandler;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class AdvancedENEQuery {
	public static final Map<String, String> DEFAULT_MODES = new HashMap<String, String>();
	private String DxDefault;
	private String NtxDefault;
	private int numRecs;
	private int bthresh;
	private List<ParamHandler> paramHandlers;
	protected ENEConnection conn;
	private QueryHandler qh;
	private static final Logger logger = Logger.getLogger(AdvancedENEQuery.class);

	public AdvancedENEQuery(String queryString, ENEConnection connection)
			throws ENEException {
		DEFAULT_MODES.put("matchany", null);
		DEFAULT_MODES.put("matchall", null);
		DEFAULT_MODES.put("matchallpartial", null);
		DEFAULT_MODES.put("matchallany", null);
		DEFAULT_MODES.put("matchpartial", null);
		DEFAULT_MODES.put("matchpartialmax", null);
		DEFAULT_MODES.put("matchboolean", null);

		this.DxDefault = "matchallpartial";
		this.NtxDefault = "matchallpartial";
		this.numRecs = 10;
		this.bthresh = 2000;

		this.paramHandlers = new ArrayList<ParamHandler>();

		if (connection == null) {
			throw new ENEException("ENEConnection cannot be null");
		}
		if (queryString == null) {
			throw new ENEException(
					"Query string must provide at least R, N, or Vn parameter");
		}
		this.conn = connection;
		this.qh = new QueryHandler(queryString, "UTF-8");
	}

	public void setNumRecsDefault(int num) {
		this.numRecs = num;
	}

	public int getNumRecsDefault() {
		return this.numRecs;
	}

	public void setDxDefaultMode(String mode) throws ENEException {
		if (!DEFAULT_MODES.containsKey(mode))
			throw new ENEException(mode + " is not a valid match mode");
		this.DxDefault = mode;
	}

	public String getDxDefaultMode() {
		return this.DxDefault;
	}

	public void setNtxDefaultMode(String mode) throws ENEException {
		if (!DEFAULT_MODES.containsKey(mode))
			throw new ENEException(mode + " is not a valid match mode");
		this.NtxDefault = mode;
	}

	public String getNtxDefaultMode() {
		return this.NtxDefault;
	}

	public void setBulkThreshold(int thresh) {
		this.bthresh = thresh;
	}

	public int getBulkThreshold() {
		return this.bthresh;
	}

	public void addParamHandler(ParamHandler handler) {
		if (handler != null)
			this.paramHandlers.add(handler);
	}

	public void addParamHandler(ParamHandler handler, int pos) {
		if (handler != null)
			this.paramHandlers.add(pos, handler);
	}

	public List<ParamHandler> getParamHandlers() {
		return this.paramHandlers;
	}

	private void setDaysFromTodayRange() {
		if (this.qh.getParam("Nf") != null) {
			String[] ranges = this.qh.getParam("Nf").split("\\|\\|");
			Date curDate = new Date();
			for (int i = 0; i < ranges.length; ++i) {
				if (ranges[i].indexOf("DAYS_FROM_TODAY") > 0) {
					String[] parts = ranges[i].split("\\|");
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					if (parts[0].indexOf("DTG") >= 0)
						df = new SimpleDateFormat("yyyyMMddHHmm");
					int numDays = Integer.parseInt(parts[1].replaceFirst(
							"DAYS_FROM_TODAY ", ""));
					Calendar endTime = new GregorianCalendar();
					endTime.setTimeInMillis(curDate.getTime());
					endTime.add(5, -numDays);
					Date oldDate = new Date(endTime.getTimeInMillis());
					ranges[i] = (parts[0] + "|BTWN " + df.format(oldDate) + " " + df
							.format(curDate));
				}
			}
			String goodRange = "";
			for (int i = 0; i < ranges.length; ++i) {
				goodRange = goodRange + ranges[i];
				if (i < ranges.length - 1)
					goodRange = goodRange + "||";
			}
			this.qh.addParam("Nf", goodRange);
		}
	}

	@SuppressWarnings("unchecked")
	private void setValueBasedNavDescriptors() throws ENEQueryException {
		if (this.qh.getParam("Vn") != null) {
			String navParms = this.qh.getParam("Vn");

			String[] navParts = navParms.split("\\|\\|");
			String navDescs = this.qh.getParam("N");
			if (navDescs == null) {
				navDescs = "0";
			}
			for (int j = 0; j < navParts.length; ++j) {
				String[] termParts = navParts[j].split("\\|");
				if (termParts.length != 2)
					continue;
				ENEQuery dimqry = new ENEQuery();
				dimqry.setNavNumERecs(0);
				dimqry.setNavAllRefinements(false);
				dimqry.setDimSearchTerms(termParts[1]);
				ENEQueryResults dimqr = this.conn.query(dimqry);

				DimensionSearchResult dsr = dimqr.getDimensionSearch();

				Iterator<DimensionSearchResultGroup> grpIter = dsr.getResults()
						.iterator();
				boolean notDone = true;
				while ((grpIter.hasNext()) && (notDone)) {
					DimensionSearchResultGroup dsrg = grpIter.next();

					Iterator<DimVal> rootIter = dsrg.getRoots().iterator();

					int rootPos = 0;
					while ((rootIter.hasNext()) && (notDone)) {
						DimVal root = rootIter.next();
						if (termParts[0].equals(root.getName())) {
							Iterator<DimLocation> dll = ((DimLocationList) dsrg
									.get(rootPos)).iterator();
							while ((dll.hasNext()) && (notDone)) {
								DimLocation dloc = dll.next();
								if (termParts[1].equals(dloc.getDimValue()
										.getName())) {
									String newId = Long.toString(dloc
											.getDimValue().getId());
									if (navDescs.indexOf(newId) < 0) {
										navDescs = navDescs + " " + newId;
									}
									notDone = false;
								}
							}
						}
						++rootPos;
					}
				}
			}

			this.qh.addParam("N", navDescs);
		}
	}

	private void setDimSearchNavDescriptors() {
		if ((this.qh.getParam("Dn") == null) && (this.qh.getParam("D") != null)) {
			this.qh.addParam("Dn", "0");
			if (this.qh.getParam("N") != null)
				this.qh.addParam("Dn", this.qh.getParam("N"));
		}
	}

	private void setNegativeNavDescriptors() {
		if (this.qh.getParam("Nn") != null) {
			String[] ids = this.qh.getParam("Nn").split("\\|\\|");
			String Nr = "AND(NOT(OR(";
			for (int i = 0; i < ids.length; ++i) {
				String[] parts = ids[i].split("\\|");
				Nr = Nr + parts[0] + ",";
			}
			Nr = Nr.replaceFirst(",$", "");
			Nr = Nr + "))";
			if (this.qh.getParam("Nr") != null)
				Nr = Nr + "," + this.qh.getParam("Nr");
			Nr = Nr + ")";
			this.qh.addParam("Nr", Nr);
		}
	}

	private void setDisplayAllRefinements(ENEQuery query) {
		if ((this.qh.getParam("Se") == null)
				|| (this.qh.getParam("Se").equals("1")))
			query.setNavAllRefinements(true);
	}

	private void setReturnProps(ENEQuery query) {
		if (this.qh.containsParam("Sp")) {
			FieldList fl = new FieldList();
			String[] props = this.qh.getParam("Sp").split("\\|\\|");
			for (int i = 0; i < props.length; ++i) {
				if (!props[i].matches("^\\s*$"))
					fl.addField(props[i]);
			}
			if (!fl.isEmpty())
				query.setSelection(fl);
		}
	}

	public void setReturnProps(String props) {
		this.qh.addParam("Sp", props);
	}

	private void setNumRecs(ENEQuery query) {
		int numRequested = this.numRecs;
		if (this.qh.containsParam("Sn")) {
			numRequested = Integer.parseInt(this.qh.getParam("Sn"));
		}
		if (query.getNavRollupKey() == null) {
			if (numRequested > this.bthresh) {
				query.setNavNumBulkERecs(numRequested);
				query.setNavNumERecs(0);
			} else if (numRequested == -1) {
				query.setNavNumBulkERecs(-1);
				query.setNavNumERecs(0);
			} else {
				query.setNavNumERecs(numRequested);
			}
		} else if (numRequested > this.bthresh) {
			query.setNavNumBulkAggrERecs(numRequested);
			query.setNavNumAggrERecs(0);
		} else if (numRequested == -1) {
			query.setNavNumBulkAggrERecs(-1);
			query.setNavNumAggrERecs(0);
		} else {
			query.setNavNumAggrERecs(numRequested);
		}
	}

	public ConsolidatedQueryResult process() throws ENEQueryException {
		String host = ((HttpENEConnection) this.conn).getHostname();
		int port = ((HttpENEConnection) this.conn).getPort();

		setDaysFromTodayRange();
		setValueBasedNavDescriptors();
		setDimSearchNavDescriptors();
		setNegativeNavDescriptors();

		logger.trace(String.format("ENE PROCESS method: query --> %s", this.qh.toString()));
		ENEQuery query = new UrlENEQuery(this.qh.toString(), "UTF-8");

		setDisplayAllRefinements(query);
		setReturnProps(query);
		setNumRecs(query);

		query.setNavKeyProperties("all");

		if (this.qh.containsParam("D")) {
			query.setDimSearchCompound(true);
			query.setDimSearchRankResults(true);
		}

		ConsolidatedQueryResult result = new ConsolidatedQueryResult();
		Iterator<ParamHandler> iter = this.paramHandlers.iterator();
		while (iter.hasNext()) {
			iter.next().handle(this.qh, query, result, this.conn);
		}

		result.addResult(this.conn.query(query), query);
		return result;
	}

	public void setConnection(ENEConnection connection) {
		this.conn = connection;
	}

	public boolean dgraphIsAlive() {
		String host = ((HttpENEConnection) this.conn).getHostname();
		int port = ((HttpENEConnection) this.conn).getPort();
		try {
			URL localURL = new URL("http", host, port, "/admin?op=stats");
			localURL.getContent();
		} catch (IOException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			return false;
		}
		return true;
	}
}