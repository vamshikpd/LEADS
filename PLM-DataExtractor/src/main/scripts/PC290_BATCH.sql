-- Program:   PC290_BATCH.sql
-- Date:      03/22/2010
-- Purpose:   Identify changes in parolees with classification of HRSO: 
--            1.  Insert parolees with PC290_REQ= 'Y' and classification_code = 'HR' (HRSO) 
--                into hrso_hist who are not presently in the table; 
--                   set hrso_ date to today's date; 
--                   set drop_date to sysdate+15.  
--            2.  Delete from hrso_hist any parolees who do not (no longer) have PC290_REQ= 'Y' 
--                and classification_code = 'HR' (HRSO).  
--            3.  Re-compile and refresh PC290_Report Materialized View
--            4.  Re-compile and refresh the CITY and COUNTY Materialized Views
--            To better describe functionality of this program, I renamed it from UpdateHRSO_HIST.sql to PC290_BATCH.sql 
--            5.  Before Refresh of PC290_REPORT Materialized View, 
--            Add in capability to report CDC_Nums for Parolees whose Revocation Release dates are more recent than their parole dates
--            (i.e. check for revoc_rels_date < parole_date).
--            6.  Replace = 'HR' w/ in ('HR','HX','AS','AX','PS','PX') to capture all new PC290/High Risk Sex Offendors in our Audit
--            7.  Add an update script to handle chages between two sets of Sex Offenders:
--                those w/out GPS (HR and HX) and those with GPS (AS, AX, PS and PX)  
--                For example, if a parolee with a class_code of HR or HX changes to an AS, AX, PS or PX , or vice-versa, we need to 
--                update the HRSO_HIST table and set the HRSO_DATE, DROP_DATE to SySDATE and SYSDATE+15 and the CLASS_CODE to the new 
--                CLASS_CODE value in PAROLEE. If a change occurs between sets, where for example, an HR chnages to HX, or an AX to a PX,
--                then we simply update the HRSO_HIST table with the values already stored in it; this means that the CLASS_CODE column
--                will not reflect the new class code of HX, if changed from HR in the PAROLEE table.
--            8.  General cleanup
--            9.  Add refresh of new Jessica's Law Materialized View:  PC290_DISCHARGE_REPORT.
--            10. Add email notification to EIS DBA and EIS Parole Leads Support of success/failure of job and job log.
--            11. Merge the Geo-Coder and PC290_BATCH Jobs into the Leads Load Batch Job.
--            12. Add date/time to start and end of program
--            13. Add Refresh of SMT_REPORT_MV Materialized View that consolidates SMT data from Cal Parole and Leads Photo Capture into 
--                a single view for Leads Summary and Detail Reports that include an SMT Search. 

--	spool &1
set linesize 200
set pagesize 100
   
   PROMPT Starting PC290 Batch...
   select to_char(sysdate, 'DD-MON-YYYY HH:MI:SS') from dual;  
   
   -- Insert the parolees who have pc290_req = 'Y' and class_code equal to 'HR' 
   -- and that are not presently in the HRSO_HIST table 
   -- (Outer join history table with parolee table.  
   -- New ones will have a null cdc_num)

   --   First report the CDC Numbers for the new records to be inserted
  
   PROMPT New Parolee CDC Numbers to be added to the HRSO_HIST table...  
      SELECT 
        p.cdc_num, 
        sysdate, 
        sysdate+15, 
        p.classification_code
      FROM 
        cpowner.parolee p,
        cpowner.PLM_cdc PLM, 
        cpowner.hrso_hist h
      WHERE
        p.cdc_num = PLM.cdc_num 
        AND p.pc_290_req = 'Y'
        AND p.classification_code IN ('HR','HX','AS','AX','PS','PX')
        AND p.cdc_num = h.cdc_num(+)
        AND h.cdc_num IS NULL
      ORDER BY 
      	cdc_num DESC;  
   

   PROMPT Inserting the new CDC Numbers... 
   INSERT INTO hrso_hist (
      SELECT 
        p.cdc_num, 
        sysdate, 
        sysdate+15, 
        p.classification_code
      FROM 
        cpowner.parolee p,
        cpowner.PLM_cdc PLM, 
        cpowner.hrso_hist h
      WHERE
        p.cdc_num = PLM.cdc_num 
        AND p.pc_290_req = 'Y'
        AND p.classification_code IN ('HR','HX','AS','AX','PS','PX')
        AND p.cdc_num = h.cdc_num(+)
        AND h.cdc_num IS NULL
   );

   COMMIT;


   --Show CDC Number, HRSO Date, Drop Date and Orginial Class Code 
   --for each Parolee whose class_code, hrso_date and drop_date will be 
   --updated in the HRSO_HIST Audit Table because his class_code in the Parolee Table
   --changed from the set of (HR, HX) to that of (AS, AX, PS, PX).
   --Also, show CDC Numbers for each Parolee whose class_code, hrso_date and drop_date will
   --be updated to the same value they already have in the HRSO_HIST table, because his class
   --code in the Parolee Table changed within sets (e.g. between HR and "HX or between AX and PX) 
   PROMPT Show CDC Numbers, Hrso and Drop Dates, and Original Class Code 
   PROMPT of Parolees to be updated per new GPS Classification Code Rules 
   PROMPT   (If class_code changed between sets (e.g. from HR/HX to AS/AX/PS/PX or vice versa),
   PROMPT     then HRSO class code will be updated with the new class_code
   PROMPT   If class_code changed within sets (e.g. from HR to HX),
   PROMPT     then HRSO class_code will be updated with the same value it already has):
   
   column PAROLEE_CDC format A11
   column HRSO_CDC format A8
   column PAROLEE_CLASS format A13
   column HRSO_CLASS format A10
   column HRSO_DATE format A20
   column DROP_DATE format A20
   column NEW_HRSO_DATE format A20
   column NEW_DROP_DATE format A20
   column NEW_CLASS format A9

	SELECT
	p.cdc_num AS "PAROLEE_CDC", 
	hh.cdc_num AS "HRSO_CDC",
	to_char(hh.hrso_date, 'DD-MM-YYYY HH:MI:SS') AS "HRSO_DATE", 
	to_char(hh.drop_date, 'DD-MM-YYYY HH:MI:SS') AS "DROP_DATE",
	p.classification_code AS "PAROLEE_CLASS", hh.class_code AS "HRSO_CLASS",
		CASE
		  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
		    THEN sysdate
		  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
		    THEN sysdate
		  ELSE hh.hrso_date
		 END AS "SYSDATE",
		CASE
		  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
		    THEN sysdate+15
		  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
		    THEN sysdate+15
		  ELSE hh.drop_date
		END AS "SYSDATE+15",
		CASE
		  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
		    THEN p.classification_code
		  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
		    THEN p.classification_code
		  ELSE hh.class_code
		END AS "CLASS_CODE"
    FROM 
    	cpowner.parolee p,
    	cpowner.PLM_cdc PLM,
    	cpowner.hrso_hist hh
	WHERE 
		p.cdc_num = PLM.cdc_num
		AND p.cdc_num = hh.cdc_num
		AND p.pc_290_req = 'Y'
		AND p.classification_code IN ('HR','HX','AS','AX','PS','PX')
		AND p.classification_code != hh.class_code
	ORDER BY 
		p.classification_code DESC;

   --Update records where class_code changed from HR/HX to AS, AX, PS or PX vice versa

   PROMPT Updating new CDC Numbers where class_code changed from HR/HX to AS, AX, PS or PX vice versa...
   	UPDATE 
   		cpowner.hrso_hist hh
    SET (hrso_date, drop_date, class_code) = 
      (
      	SELECT
			CASE
			  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
			    THEN sysdate
			  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
			    THEN sysdate
			  ELSE hh.hrso_date
			 END AS "SYSDATE",
			CASE
			  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
			    THEN sysdate+15
			  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
			    THEN sysdate+15
			  ELSE hh.drop_date
			END AS "SYSDATE+15",
			CASE
			  WHEN (p.classification_code IN ('AX','AS','PS','PX') AND hh.class_code IN ('HR','HX'))
			    THEN p.classification_code
			  WHEN (p.classification_code IN ('HR','HX') AND hh.class_code IN ('AX','AS','PS','PX'))
			    THEN p.classification_code
			  ELSE hh.class_code
			END AS "CLASS_CODE"
        FROM 
        	cpowner.parolee p,
        	cpowner.PLM_cdc PLM
		WHERE 
			p.cdc_num = PLM.cdc_num
			AND p.cdc_num = hh.cdc_num
			AND p.pc_290_req = 'Y'
			AND p.classification_code IN ('HR','HX','AS','AX','PS','PX')
			AND p.classification_code != hh.class_code
      )
    WHERE EXISTS
      (
      	SELECT 
      		p.cdc_num 
      	FROM 
      		cpowner.parolee p,
      		cpowner.PLM_cdc PLM
         WHERE 
         	p.cdc_num = PLM.cdc_num
         	AND p.cdc_num = hh.cdc_num
			AND p.pc_290_req = 'Y'
			AND p.classification_code IN ('HR','HX','AS','AX','PS','PX')
			AND p.classification_code != hh.class_code
      );
   COMMIT;

   
   -- Delete from the hrso_hist history table the parolees whose class_code changed from HRSO/GPS
   -- to some other class_code or who are no longer PC290's (i.e. pc290_req != 'Y')

   --   First report the CDC Numbers for the Parolees to be deleted
   PROMPT CDC Numbers for Parolees changed to non-PC290 or non-HRSO/GPS to be deleted from HRSO_HIST table...

   SELECT * FROM hrso_hist
      WHERE cdc_num IN (SELECT h.cdc_num FROM hrso_hist h, parolee p
         WHERE h.cdc_num = p.cdc_num
         AND (p.pc_290_req != 'Y' OR p.classification_code NOT IN ('HR','HX','AS','AX','PS','PX')
           OR p.classification_code IS NULL));


   PROMPT Deleting any CDC Numbers for Parolees changed to non-PC290 or non-HRSO/GPS...

   DELETE FROM hrso_hist
      WHERE cdc_num IN (SELECT h.cdc_num FROM hrso_hist h, parolee p
         WHERE h.cdc_num = p.cdc_num
         AND (p.pc_290_req !='Y' OR p.classification_code NOT IN ('HR','HX','AS','AX','PS','PX')
           OR p.classification_code IS NULL));
   COMMIT;

   
   -- After SPDB Load drops/recreates and loads the Parolee Table w/ new data from Cal Parole.
   -- Report CDC_NUMs of any Parolees whose Revocation Release Date is less than his Parole Date.
   
   PROMPT Begin PC290 Exception Report...
   PROMPT Parolees whose Revocation Date is less than Parole Date
   PROMPT And who will not be handled by the PC290_REPORT MVIEW logic:
   
   SELECT cdc_num, parole_date, revoc_rels_date 
   FROM parolee 
   WHERE revoc_rels_date < parole_date
   AND revoc_rels_date IS NOT NULL;
   
   PROMPT End PC290 Exception Report!
   PROMPT

   PROMPT PC290 Batch Complete!
   select to_char(sysdate, 'DD-MON-YYYY HH:MI:SS') from dual;

--	spool off;

exit;
