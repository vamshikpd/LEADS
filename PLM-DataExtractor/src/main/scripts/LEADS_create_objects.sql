set echo on
set feedback on
set termout on
set heading on
set verify on
set sqlprompt ""
set timing on

spool /exp/logs/&1.-&2.-LEADS_CREATE_OBJECTS.log

Alter session set current_schema = &3;

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='DATA_EXTRACT_STATUS';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE DATA_EXTRACT_STATUS';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop TABLE CPOWNER.DATA_EXTRACT_STATUS';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE TABLE CPOWNER.DATA_EXTRACT_STATUS (
    ID NUMBER NOT NULL,
    PROCESS_ID NUMBER NOT NULL,
    PROCESS_NAME VARCHAR2(30) NOT NULL,
    FILE_NAME VARCHAR2(50) NOT NULL,
    DATA_EXTRACT_UPTO_DATE DATE NOT NULL,
    PROCESS_START_DATE DATE NOT NULL,
    PROCESS_END_DATE DATE NULL,
    STATUS VARCHAR2(10) NOT NULL,
    MESSAGE VARCHAR2(900)
);

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='USER_SEARCH';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE USER_SEARCH';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop TABLE CPOWNER.USER_SEARCH';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE TABLE CPOWNER.USER_SEARCH (
  SEARCH_ID     NUMBER          NOT NULL,
  SEARCH_NAME   VARCHAR2(30)    NOT NULL,
  USERNAME      VARCHAR2(30)    NOT NULL,
  QUERY         VARCHAR2(500)   NOT NULL
);
/ 

ALTER TABLE CPOWNER.USER_SEARCH
  ADD CONSTRAINT USER_SEARCH_PK PRIMARY KEY (SEARCH_ID)
/

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='SEARCH_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE SEARCH_SEQ';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop SEQUENCE CPOWNER.SEARCH_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE SEQUENCE CPOWNER.SEARCH_SEQ
  MINVALUE 1
  MAXVALUE 999999999999999999999999999
  INCREMENT BY 1
  NOCYCLE
  ORDER
  CACHE 20
/

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='DATA_EXTRACT_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE DATA_EXTRACT_SEQ';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop SEQUENCE CPOWNER.DATA_EXTRACT_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE SEQUENCE CPOWNER.DATA_EXTRACT_SEQ
MINVALUE 1
MAXVALUE 99999999
INCREMENT BY 1
NOCYCLE
NOORDER
NOCACHE
/

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='DATA_EXTRACT_PROCESS_ID_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE DATA_EXTRACT_PROCESS_ID_SEQ';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop SEQUENCE CPOWNER.DATA_EXTRACT_PROCESS_ID_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE SEQUENCE CPOWNER.DATA_EXTRACT_PROCESS_ID_SEQ
MINVALUE 1
MAXVALUE 99999999
INCREMENT BY 1
NOCYCLE
NOORDER
NOCACHE
/

-----------------------------------------------------------------------------

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='CORI_INFO';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE CORI_INFO';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop TABLE CPOWNER.CORI_INFO';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE TABLE CPOWNER.CORI_INFO
(
QUERY_ID NUMBER NOT NULL ENABLE, 
USERNAME VARCHAR2(30 BYTE) NOT NULL ENABLE, 
IP_ADDRESS VARCHAR2(15 BYTE) NOT NULL ENABLE, 
CDC_NUM VARCHAR2(6 BYTE) NOT NULL ENABLE, 
INQUIRY_DATE DATE NOT NULL ENABLE, 
CASE_NO VARCHAR2(30 BYTE), 
REASON_NO VARCHAR2(30 BYTE)
);

ALTER TABLE CPOWNER.CORI_INFO
  ADD CONSTRAINT CORI_INFO_PK PRIMARY KEY (QUERY_ID)
/
 

CREATE INDEX CPOWNER.CORI_INFO_IDX ON CPOWNER.CORI_INFO (USERNAME, INQUIRY_DATE);
 
-----------------------------------------------------------------------------

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='CORI_QUERY';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE CORI_QUERY';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop TABLE CPOWNER.CORI_QUERY';
        exception
            when others then null;
        end;
    end if;
end;
/

CREATE TABLE CPOWNER.CORI_QUERY 
(    
QUERY_ID NUMBER NOT NULL ENABLE, 
USERNAME VARCHAR2(30 BYTE) NOT NULL ENABLE, 
IP_ADDRESS VARCHAR2(15 BYTE) NOT NULL ENABLE, 
INQUIRY_DATE DATE NOT NULL ENABLE, 
CASE_NO VARCHAR2(30 BYTE), 
REASON_NO VARCHAR2(30 BYTE), 
SEARCH_TYPE CHAR(4 BYTE) NOT NULL ENABLE
);

ALTER TABLE CPOWNER.CORI_QUERY
  ADD CONSTRAINT CORI_QUERY_PK PRIMARY KEY (QUERY_ID)
/
 

-------------------------------------------------------------------------------

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='CORI_QUERY_RESULTS';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE CORI_QUERY_RESULTS';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop TABLE CPOWNER.CORI_QUERY_RESULTS';
        exception
            when others then null;
        end;
    end if;
end;
/

CREATE TABLE CPOWNER.CORI_QUERY_RESULTS 
(    
QUERY_ID NUMBER NOT NULL ENABLE, 
CDC_NUM VARCHAR2(6 BYTE) NOT NULL ENABLE
) ;

ALTER TABLE CPOWNER.CORI_QUERY_RESULTS
  ADD CONSTRAINT CORI_QUERY_RESULTS_PK PRIMARY KEY (QUERY_ID, CDC_NUM)
/


CREATE INDEX CPOWNER.CORI_QUERY_RESULTS_IDX ON CPOWNER.CORI_QUERY_RESULTS (QUERY_ID);

----------------------------------------------------------------------------------

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='CORI_INFO_ID_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE CORI_INFO_ID_SEQ';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop SEQUENCE CPOWNER.CORI_INFO_ID_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/

CREATE SEQUENCE  CPOWNER.CORI_INFO_ID_SEQ  MINVALUE 1 MAXVALUE 1000000000000000000000000000 INCREMENT BY 1 START WITH 7551804 NOCACHE  ORDER  NOCYCLE ;


declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='CORI_QUERY_ID_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE CORI_QUERY_ID_SEQ';
        exception
            when others then null;
        end;
        begin
            execute immediate 'drop SEQUENCE CPOWNER.CORI_QUERY_ID_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE SEQUENCE  CPOWNER.CORI_QUERY_ID_SEQ  MINVALUE 20000 MAXVALUE 1000000000000000000000000000 INCREMENT BY 1 START WITH 208650983 NOCACHE  ORDER  NOCYCLE ;

----------------------------------------------------------------------------------

begin
    begin
        execute immediate 'drop VIEW PLM_SMT_VIEW';
    exception
        when others then null;
    end;
end;
/
CREATE OR REPLACE VIEW CPOWNER.PLM_SMT_VIEW (
  CDC_NUM,
  SMT_CODE,
  SMT_DESC,
  SMT_PICTURE,
  SMT_TEXT,
  CREATED_DATE,
  LAST_CHG_DATE
) AS
SELECT 
    PLM.CDC_NUM, 
    S.SMT_CODE, 
    T.SMT_DESC,
    S.SMT_PICTURE, 
    S.SMT_TEXT, 
    S.CREATED_DATE, 
    --S.LAST_CHG_DATE
    CASE
        WHEN S.LAST_CHG_DATE IS NULL
        THEN S.CREATED_DATE
        ELSE S.LAST_CHG_DATE
    END AS "LAST_CHG_DATE"
FROM 
    CPOWNER.SMT S, 
    CPOWNER.SMT_TYPE T, 
    CPOWNER.PLM_CDC PLM
WHERE 
    S.SMT_CODE = T.SMT_CODE 
    AND S.CDC_NUM = PLM.CDC_NUM
UNION
SELECT 
    I.CDC_NUM AS CDC_NUM, 
    I.SUBTYPE AS SMT_CODE, 
    I.SUBTYPE AS SMT_DESC,
    'PCW' AS SMT_PICTURE, 
    I.DESCR AS SMT_TEXT, 
    INSERT_DATE AS CREATED_DATE, 
    --UPDATE_DATE AS LAST_CHG_DATE
    CASE
        WHEN UPDATE_DATE IS NULL
        THEN INSERT_DATE
        ELSE UPDATE_DATE
    END AS "LAST_CHG_DATE"
FROM 
    CPOWNER.IMAGE_INFO I, 
    CPOWNER.PLM_CDC PLM
WHERE 
    I.TYPE = 2 
    AND I.CDC_NUM = PLM.CDC_NUM;
/    

begin
    begin
        execute immediate 'drop VIEW PLM_PAROLEE_VIEW';
    exception
        when others then null;
    end;
end;
/
CREATE OR REPLACE VIEW CPOWNER.PLM_PAROLEE_VIEW (
  CDC_NUM,
  UNIT_CODE,
  REVOC_RELS_DATE,
  PAROLE_DATE,
  DISCHARGED_DATE,
  PC_290_REQ,
  CLASSIFICATION_CODE,
  STATUS_CODE,
  LAST_CHG_DATE
) AS
SELECT
    P.CDC_NUM,
    P.UNIT_CODE,
    P.REVOC_RELS_DATE,
    P.PAROLE_DATE,
    P.DISCHARGED_DATE,
    P.PC_290_REQ,
    P.CLASSIFICATION_CODE,
    P.STATUS_CODE,
    P.LAST_CHG_DATE
FROM 
    CPOWNER.PAROLEE P
WHERE 
    P.CDC_NUM NOT BETWEEN 'I30000' AND 'I99999'
    AND SUBSTR(P.CDC_NUM,1,1) IN ('A','B','C','D','E','F','G','H','I','J','K','N','P','R','T','X','W','V')
    AND (
        NVL(P.STATUS_CODE,'x') IN ('50','55','60','65')
        OR (
            DISCHARGED_DATE is not null
            and (
                (
                    STATUS_CODE = '70'
                    and DISCHARGED_DATE > sysdate - 366
                )
				or 
                (
                    STATUS_CODE = '70'
                    and UNIT_CODE like 'INS%'
                    and DISCHARGED_DATE > to_date('03182009', 'MMDDYYYY')
                    and DISCHARGED_DATE > sysdate - 1098
                )
            )
        )
        OR (
            NVL(P.STATUS_CODE,'x') = '70'
            AND P.UNIT_CODE LIKE 'INS%'
            AND NVL(P.DISCHARGED_DATE,SYSDATE) > TO_DATE('03182009','MMDDYYYY')                                                                                                                                                                                                                      
            AND NVL(P.DISCHARGED_DATE,SYSDATE) > SYSDATE-1098
        )
        OR (
            NVL(P.STATUS_CODE,'x') = '10'
            AND NVL(P.PAROLE_DATE,SYSDATE) < SYSDATE+120
        )
    );
/    

begin
    begin
        execute immediate 'drop VIEW PLM_CDC_ADDR_PC290_VIEW';
    exception
        when others then null;
    end;
end;
/    
CREATE OR REPLACE VIEW CPOWNER.PLM_CDC_ADDR_PC290_VIEW AS 
    SELECT
        A.CDC_NUM,
        A.ADDRESS_EFF_DATE,
        A.LAST_CHG_DATE,
        A.CITY_NAME,
        C.COUNTY_NAME   
    FROM
        CPOWNER.PLM_CDC PLM,
        CPOWNER.ADDRESS A, 
        CPOWNER.COUNTY C,
        (
            SELECT
                CDC_NUM,
                MAX(ADDRESS_EFF_DATE) AS LATEST_ADDRESS_EFF_DATE
            FROM
                CPOWNER.ADDRESS
            GROUP BY
                CDC_NUM
        ) B
    WHERE
        A.CDC_NUM = PLM.CDC_NUM
        AND A.CDC_NUM = B.CDC_NUM
        AND A.ADDRESS_EFF_DATE = B.LATEST_ADDRESS_EFF_DATE
        AND A.COUNTY_CODE = C.COUNTY_CODE(+);
/        

begin
    begin
        execute immediate 'drop VIEW PLM_PC290_INFO_VIEW';
    exception
        when others then null;
    end;
end;
/  
CREATE OR REPLACE VIEW CPOWNER.PLM_PC290_INFO_VIEW AS 
SELECT
    DISTINCT(P.CDC_NUM),
    CASE
        WHEN P.REVOC_RELS_DATE IS NULL THEN P.PAROLE_DATE
        WHEN P.PAROLE_DATE <= P.REVOC_RELS_DATE THEN P.REVOC_RELS_DATE
        ELSE P.PAROLE_DATE
    END AS "ACTION_DATE",
    'R' AS "ACTION_TYPE",
    CASE
    WHEN A.LAST_CHG_DATE >SYSDATE-15 THEN A.LAST_CHG_DATE 
    ELSE NULL
  END AS "ADD_CHANGED_DATE"
FROM
    CPOWNER.PLM_PAROLEE_VIEW P,
    CPOWNER.PLM_CDC_ADDR_PC290_VIEW A,
    CPOWNER.HRSO_HIST H
WHERE
    P.PC_290_REQ = 'Y'
    AND P.CLASSIFICATION_CODE NOT IN ('DP','PD')
    AND NVL(P.REVOC_RELS_DATE, P.PAROLE_DATE) BETWEEN SYSDATE - 16 AND SYSDATE + 60
    AND P.CDC_NUM = A.CDC_NUM(+)
    AND P.CDC_NUM = H.CDC_NUM(+)
    AND H.DROP_DATE >= SYSDATE 
UNION
SELECT
    DISTINCT(P.CDC_NUM),
    CASE
        WHEN P.REVOC_RELS_DATE IS NULL THEN P.PAROLE_DATE
        WHEN P.PAROLE_DATE <= P.REVOC_RELS_DATE THEN P.REVOC_RELS_DATE
        ELSE P.PAROLE_DATE
    END AS "ACTION_DATE",
    'R' AS "ACTION_TYPE",
    CASE
    WHEN A.LAST_CHG_DATE >SYSDATE-15 THEN A.LAST_CHG_DATE 
    ELSE NULL
  END AS "ADD_CHANGED_DATE"
FROM
    CPOWNER.PLM_PAROLEE_VIEW P,
    CPOWNER.PLM_CDC_ADDR_PC290_VIEW A,
    CPOWNER.HRSO_HIST H
WHERE
    P.PC_290_REQ = 'Y'
    AND P.CLASSIFICATION_CODE NOT IN ('DP','PD')
    AND NVL(P.REVOC_RELS_DATE, P.PAROLE_DATE) <= SYSDATE + 60
    AND P.CDC_NUM = H.CDC_NUM(+)
    AND H.DROP_DATE >= SYSDATE 
    AND A.CDC_NUM = P.CDC_NUM
    AND A.LAST_CHG_DATE >= SYSDATE - 16
UNION
SELECT
    DISTINCT(P.CDC_NUM),
    CASE
        WHEN P.REVOC_RELS_DATE IS NULL THEN P.PAROLE_DATE
        WHEN P.PAROLE_DATE <= P.REVOC_RELS_DATE THEN P.REVOC_RELS_DATE
        ELSE P.PAROLE_DATE
    END AS "ACTION_DATE",
    'R' AS "ACTION_TYPE",
    CASE
    WHEN A.LAST_CHG_DATE >SYSDATE-15 THEN A.LAST_CHG_DATE 
    ELSE NULL
  END AS "ADD_CHANGED_DATE"
FROM
    CPOWNER.PLM_PAROLEE_VIEW P,
    CPOWNER.PLM_CDC_ADDR_PC290_VIEW A,
    CPOWNER.HRSO_HIST H
WHERE
    H.DROP_DATE >= SYSDATE
    AND P.CDC_NUM = H.CDC_NUM
    AND P.CDC_NUM = A.CDC_NUM
    AND P.CLASSIFICATION_CODE NOT IN ('DP','PD')
    AND NVL(P.REVOC_RELS_DATE, P.PAROLE_DATE) <= SYSDATE + 60
    AND SYSDATE <= H.DROP_DATE
UNION   
SELECT 
    DISTINCT(P.CDC_NUM), 
    CASE
      WHEN P.CONTROL_DISCHRG_DATE IS NULL THEN P.DISCHARGED_DATE
      WHEN P.DISCHARGED_DATE IS NULL THEN P.CONTROL_DISCHRG_DATE
      WHEN P.DISCHARGED_DATE <= P.CONTROL_DISCHRG_DATE THEN P.DISCHARGED_DATE
      ELSE P.CONTROL_DISCHRG_DATE
    END AS "ACTION_DATE",
    'D' AS "ACTION_TYPE",
    NULL AS "ADD_CHANGED_DATE"
FROM 
    CPOWNER.PAROLEE P
WHERE 
(
    CASE
        WHEN P.CONTROL_DISCHRG_DATE IS NULL THEN P.DISCHARGED_DATE
        WHEN P.DISCHARGED_DATE IS NULL THEN P.CONTROL_DISCHRG_DATE
        WHEN P.DISCHARGED_DATE <= P.CONTROL_DISCHRG_DATE THEN P.DISCHARGED_DATE
    ELSE 
        P.CONTROL_DISCHRG_DATE 
    END
) BETWEEN SYSDATE - 16 AND SYSDATE + 60
    AND P.PC_290_REQ = 'Y'
    AND P.STATUS_CODE IN ('50','65','70')
    AND P.CLASSIFICATION_CODE NOT IN ('DP','PD');
/

begin
    begin
        execute immediate 'drop PROCEDURE COLLECT_FULL_PAROLEE';
    exception
        when others then null;
    end;
end;
/ 
CREATE OR REPLACE PROCEDURE CPOWNER.COLLECT_FULL_PAROLEE
AS
BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE CPOWNER.PLM_CDC';
    COMMIT;
    INSERT INTO CPOWNER.PLM_CDC (
      SELECT CDC_NUM, MAX(LAST_CHG_DATE) AS LAST_CHG_DATE FROM (
        SELECT CDC_NUM, LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.ADDRESS B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.ALIAS B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.JOB B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.MONIKER B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.OFFENSE B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.PROBLEM_AREA B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(Greatest(Nvl(B.CREATED_DATE,to_date('01/01/1990', 'MM/DD/YYYY')), Nvl(B.LAST_CHG_DATE,to_date('01/01/1990', 'MM/DD/YYYY')))) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.SMT B          WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(Greatest(Nvl(B.INSERT_DATE ,to_date('01/01/1990', 'MM/DD/YYYY')), Nvl(B.UPDATE_DATE  ,to_date('01/01/1990', 'MM/DD/YYYY')))) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.IMAGE_INFO B  WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.SPECIAL_CONDITION B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
        UNION
        SELECT A.CDC_NUM, MAX(B.LAST_CHG_DATE) AS LAST_CHG_DATE FROM CPOWNER.PLM_PAROLEE_VIEW A, CPOWNER.VEHICLE B WHERE B.CDC_NUM=A.CDC_NUM GROUP BY A.CDC_NUM
      ) GROUP BY CDC_NUM
    );
    COMMIT;
END;
/

begin
    begin
        execute immediate 'drop FUNCTION GETGROUPCODE';
    exception
        when others then null;
    end;
end;
/ 
CREATE OR REPLACE FUNCTION CPOWNER.GETGROUPCODE(UNIT VARCHAR2)
RETURN VARCHAR2
IS
   L_STMT LONG;
   CURSOR C1
   IS
   SELECT AB3_GROUP AS GROUP_CODE FROM CPOWNER.AB3_UNIT WHERE AB3_UNIT_CODE = UNIT ORDER BY 1;
BEGIN
    L_STMT := ' ';
    FOR REC IN C1 LOOP
      IF L_STMT = ' ' THEN
        L_STMT := ''|| REC.GROUP_CODE;
      ELSE
        L_STMT := L_STMT || ',' || REC.GROUP_CODE;
      END IF;
    END LOOP;
    RETURN L_STMT;
END GETGROUPCODE;
/

begin
    begin
        execute immediate 'drop PROCEDURE LOG_PC290';
    exception
        when others then null;
    end;
end;
/
CREATE OR REPLACE PROCEDURE CPOWNER.LOG_PC290
(COUNTY IN VARCHAR2, CITY IN VARCHAR2, USERNAME IN VARCHAR2, IP_ADDRESS IN VARCHAR2, QRY_TYPE IN VARCHAR2)
IS
    CORI_QUERY_RESULTS_STMT VARCHAR2(500);
    ID_SEQ NUMBER;
    TYPE CUR_TYP IS REF CURSOR;
    C CUR_TYP;
    CDCNUM VARCHAR2(6);
BEGIN
    SELECT CPOWNER.CORI_QUERY_ID_SEQ.NEXTVAL INTO ID_SEQ FROM DUAL;

    IF QRY_TYPE = 'D' THEN
        INSERT INTO CPOWNER.CORI_QUERY VALUES (ID_SEQ,USERNAME,IP_ADDRESS,SYSDATE,'PC290_DISCHARGE_REPORT','PC290_DISCHARGE_REPORT','290J');
    ELSE
        INSERT INTO CPOWNER.CORI_QUERY VALUES (ID_SEQ,USERNAME,IP_ADDRESS,SYSDATE,'PC290_REPORT','PC290_REPORT','290');
    END IF;

    IF QRY_TYPE ='R' THEN
        CORI_QUERY_RESULTS_STMT := 'SELECT A.CDC_NUM FROM PLM_CDC_ADDR_PC290_VIEW A, CPOWNER.PLM_PC290_INFO_VIEW B, PAROLEE P WHERE P.LAST_CHG_DATE < (SELECT PROCESS_START_DATE FROM CPOWNER.DATA_EXTRACT_STATUS WHERE ID = (SELECT MAX(ID) FROM CPOWNER.DATA_EXTRACT_STATUS WHERE STATUS=' || '''' || 'SUCCESS' || '''' || ')) AND (B.ACTION_TYPE= ' || ''''|| QRY_TYPE || '''' || ' OR B.ADD_CHANGED_DATE IS NOT NULL) AND A.CDC_NUM=B.CDC_NUM AND P.CDC_NUM=B.CDC_NUM';
    ELSE                                                                                                                                                                                                                                                                                                                                                     
        CORI_QUERY_RESULTS_STMT := 'SELECT A.CDC_NUM FROM PLM_CDC_ADDR_PC290_VIEW A, CPOWNER.PLM_PC290_INFO_VIEW B, PAROLEE P WHERE P.LAST_CHG_DATE < (SELECT PROCESS_START_DATE FROM CPOWNER.DATA_EXTRACT_STATUS WHERE ID = (SELECT MAX(ID) FROM CPOWNER.DATA_EXTRACT_STATUS WHERE STATUS=' || '''' || 'SUCCESS' || '''' || ')) AND B.ACTION_TYPE= ' || '''' || QRY_TYPE || '''' || ' AND A.CDC_NUM=B.CDC_NUM AND P.CDC_NUM=B.CDC_NUM';
    END IF;

    IF CITY != '' THEN
        CORI_QUERY_RESULTS_STMT := CORI_QUERY_RESULTS_STMT || ' AND A.COUNTY_NAME=' || ''''|| COUNTY || '''' || ' AND A.CITY_NAME= ' || '''' || CITY || ''''; 
    ELSE 
        CORI_QUERY_RESULTS_STMT := CORI_QUERY_RESULTS_STMT || ' AND A.COUNTY_NAME=' || ''''|| COUNTY || '''';
    END IF;   

    OPEN C FOR CORI_QUERY_RESULTS_STMT;
    LOOP
        FETCH C INTO CDCNUM;         
        EXIT WHEN C%NOTFOUND;
        INSERT INTO CPOWNER.CORI_QUERY_RESULTS VALUES (ID_SEQ,CDCNUM);
    END LOOP;

    CLOSE C;
    COMMIT;
END;
/

begin
    begin
        execute immediate 'drop FUNCTION GETCOUNTIESFORUNIT';
    exception
        when others then null;
    end;
end;
/
CREATE OR REPLACE FUNCTION CPOWNER.GETCOUNTIESFORUNIT(UNIT VARCHAR2)
RETURN VARCHAR2
IS
   L_STMT LONG;
   CURSOR C1
   IS
   SELECT COUNTY_CODE FROM CPOWNER.JURISDICTION_CODES WHERE UNIT_CODE=UNIT ORDER BY 1;
BEGIN
    L_STMT := ' ';
    FOR REC IN C1 LOOP
      IF L_STMT = ' ' THEN
        L_STMT := ':'|| REC.COUNTY_CODE;
      ELSE
        L_STMT := L_STMT || ':' || REC.COUNTY_CODE;
      END IF;
    END LOOP;
    L_STMT := L_STMT || ':';
    RETURN L_STMT;
END GETCOUNTIESFORUNIT;
/

begin
    begin
        execute immediate 'drop FUNCTION GETCOLLRCOUNTYNAME';
    exception
        when others then null;
    end;
end;
/
CREATE OR REPLACE FUNCTION CPOWNER.GETCOLLRCOUNTYNAME (COLLR_CODE VARCHAR2)
RETURN VARCHAR2
IS
   CODE VARCHAR2(6);
   CNAME VARCHAR2(20);
BEGIN
      IF Length(COLLR_CODE) = 5 THEN
        CODE := SubStr(COLLR_CODE,1,2);
      ELSIF Length(COLLR_CODE) = 6 THEN
        CODE := SubStr(COLLR_CODE,1,3);
      ELSE
        CODE := SubStr(COLLR_CODE,1,3);
      END IF;

      SELECT COUNTY_NAME INTO CNAME FROM CPOWNER.COUNTY WHERE COUNTY_CODE = CODE;

    RETURN CNAME;
END;

/

--===================================================================================
--------------------------------------------------------
--  DDL for Table KEYVALSTORE
--------------------------------------------------------

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='KEYVALSTORE';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE CPOWNER.KEYVALSTORE';
        exception
            when others then null;
        end;
    end if;
end;
/

CREATE TABLE "CPOWNER"."KEYVALSTORE" 
(	"KEY" VARCHAR2(50 BYTE), 
"VALUE" VARCHAR2(1000 BYTE)
) ;
/
--------------------------------------------------------
--  DDL for Index KEYVALSTORE_PK
--------------------------------------------------------

CREATE UNIQUE INDEX "CPOWNER"."KEYVALSTORE_PK" ON "CPOWNER"."KEYVALSTORE" ("KEY");
/
--------------------------------------------------------
--  Constraints for Table KEYVALSTORE
--------------------------------------------------------

ALTER TABLE "CPOWNER"."KEYVALSTORE" ADD CONSTRAINT "KEYVALSTORE_PK" PRIMARY KEY ("KEY") ENABLE;
ALTER TABLE "CPOWNER"."KEYVALSTORE" MODIFY ("KEY" NOT NULL ENABLE);
/
  
insert into KEYVALSTORE values ('ACCOUNT_MANAGEMENT_URL', 'https://leads-idm.cdcr.ca.gov/iam/im/identityEnv/');
insert into KEYVALSTORE values ('CHANGE_PASSWORD_URL', 'https://leads-idm.cdcr.ca.gov/sigma/app/index#/modules/799//form');
insert into KEYVALSTORE values ('CANT_SIGN_IN_URL', 'https://leads-idm.cdcr.ca.gov/sigma/app/index#/forgot-password');
/

--===================================================================================
--------------------------------------------------------
--  DDL for Table LEADS_WS_AUDIT_LOG
--------------------------------------------------------
declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='LEADS_WS_AUDIT_LOG_SEQ';
    if CNT>0 then 
        begin
            execute immediate 'drop SEQUENCE CPOWNER.LEADS_WS_AUDIT_LOG_SEQ';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE SEQUENCE CPOWNER.LEADS_WS_AUDIT_LOG_SEQ
  MINVALUE 1
  MAXVALUE 999999999999999999999999999
  INCREMENT BY 1
  NOCYCLE
  ORDER
  CACHE 20;
/

declare CNT integer;
begin
    select count(*) into CNT from ALL_OBJECTS where OBJECT_NAME='LEADS_WS_AUDIT_LOG';
    if CNT>0 then 
        begin
            execute immediate 'drop TABLE CPOWNER.LEADS_WS_AUDIT_LOG';
        exception
            when others then null;
        end;
    end if;
end;
/
CREATE TABLE CPOWNER.LEADS_WS_AUDIT_LOG (
    ID 				NUMBER NOT NULL,
    TIME_STAMP 		TIMESTAMP NOT NULL,
    USERNAME 		VARCHAR2(30), 
	IP_ADDRESS 		VARCHAR2(15), 
    WS_TYPE  		VARCHAR2(25), 
	STATUS			VARCHAR2(15),
	QUERY			CLOB
);
ALTER TABLE CPOWNER.LEADS_WS_AUDIT_LOG
  ADD CONSTRAINT LEADS_WS_LOG_PK PRIMARY KEY (ID);
/

CREATE INDEX CPOWNER.LEADS_WS_LOG_IDX ON CPOWNER.LEADS_WS_AUDIT_LOG (TIME_STAMP);

---------------------------------------------------
COMMIT;
---------------------------------------------------

set timing off
spool off
exit;