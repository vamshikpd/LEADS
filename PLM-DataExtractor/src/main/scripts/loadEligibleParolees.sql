spool &1
-- connect to Leads as ab3x
set linesize 200
set pagesize 100
   
   PROMPT Starting FULL PAROLEE Record collection
   select to_char(sysdate, 'DD-MON-YYYY HH:MI:SS') from dual;  
   
   exec collect_full_parolee;

   PROMPT FULL PAROLEE Record collection complete!
   select to_char(sysdate, 'DD-MON-YYYY HH:MI:SS') from dual;

spool off;
exit;
