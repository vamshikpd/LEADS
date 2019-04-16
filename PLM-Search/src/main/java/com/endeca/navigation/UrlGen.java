package com.endeca.navigation;
import java.util.*;
public class UrlGen {

    private String encoding;
    private String baseUrl;
    private HashMap nameMultiMap;

    /**
     * @deprecated Method UrlGen is deprecated
     */

    public UrlGen()
    {
        encoding = "windows-1252";
        baseUrl = "";
        nameMultiMap = new HashMap();
    }

    /**
     * @deprecated Method UrlGen is deprecated
     */

    public UrlGen(String s)
    {
        encoding = "windows-1252";
        baseUrl = s;
        nameMultiMap = parse(s);
    }

    public UrlGen(String s, String s1)
    {
        baseUrl = s;
        encoding = s1;
        nameMultiMap = parse(s);
    }

    public void appendParam(String s, String s1)
    {
        if(s == null || s1 == null || s.equals("") || s1.equals(""))
        {
            return;
        }
        ArrayList arraylist = (ArrayList)nameMultiMap.get(s);
        if(arraylist == null)
        {
            arraylist = new ArrayList(1);
        }
        arraylist.add(OptiBackend.urlEncode(s1, encoding));
        nameMultiMap.put(s, arraylist);
    }

    public void appendParams(Collection collection)
    {
        Iterator iterator = collection.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s = (String)iterator.next();
            String s1 = null;
            String s2 = null;

            StringTokenizer stringtokenizer = new StringTokenizer(s, "=");
            if(stringtokenizer.hasMoreTokens())
            {
                s1 = stringtokenizer.nextToken();
            }
            if(stringtokenizer.hasMoreTokens())
            {
                s2 = stringtokenizer.nextToken();
            }
            if(s1 != null || s2 != null)
            {
                ArrayList arraylist = (ArrayList)nameMultiMap.get(s1);
                if(arraylist == null)
                {
                    arraylist = new ArrayList(1);
                }
                arraylist.add(s2);
                nameMultiMap.put(s1, arraylist);
            }
        } while(true);
    }

    public void addParam(String s, String s1)
    {
        if(s == null || s1 == null || s.equals("") || s1.equals(""))
        {
            return;
        } else
        {
            ArrayList arraylist = new ArrayList(1);
            arraylist.add(OptiBackend.urlEncode(s1, encoding));
            nameMultiMap.put(s, arraylist);
            return;
        }
    }

    public void addParams(Collection collection)
    {
        Iterator iterator = collection.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s = (String)iterator.next();
            String s1 = null;
            String s2 = null;
            StringTokenizer stringtokenizer = new StringTokenizer(s, "=");
            if(stringtokenizer.hasMoreTokens())
            {
                s1 = stringtokenizer.nextToken();
            }
            if(stringtokenizer.hasMoreTokens())
            {
                s2 = stringtokenizer.nextToken();
            }
            if(s1 != null || s2 != null)
            {
                ArrayList arraylist = new ArrayList(1);
                arraylist.add(s2);
                nameMultiMap.put(s1, arraylist);
            }
        } while(true);
    }

    public void removeParam(String s)
    {
        nameMultiMap.remove(s);
    }

    public void removeParams(Collection collection)
    {
        String s;
        for(Iterator iterator = collection.iterator(); iterator.hasNext(); nameMultiMap.remove(s))
        {
            s = (String)iterator.next();
        }

    }

    private static HashMap parse(String s)
    {
        HashMap hashmap = new HashMap();
        if(s == null)
        {
            return hashmap;
        }        
        // replace all existence of '+/-' to '%2B%2F-'
        s=s.replaceAll("\\Q+/-\\E", "%2B%2F-");

        // replace all existence of '|' to '%7C'
        s=s.replaceAll("\\Q|\\E", "%7C"); // emil 2018-03-06 Fix | character issue in Tomcat
        
        StringTokenizer stringtokenizer = new StringTokenizer(s, "&");
        do
        {
            if(!stringtokenizer.hasMoreTokens())
            {
                break;
            }
            String s1 = stringtokenizer.nextToken();
            // replace double quotes with '%22'
            s1 = s1.replace("\"", "%22");
            String s2 = null;
            String s3 = null;
			String strFinal = "";

			// replace all '>=' or '%3E=' to '%3E%3D'
			String[] str = s1.split("(>=)|(%3E=)");
			if(str.length > 1) {
				for(int i = 0; i < str.length ; i++) {
					strFinal = strFinal + str[i];
					if( i < str.length - 1) {	
						strFinal = strFinal + "%3E%3D";
					}
				}
				s1 = strFinal;
			}

			// replace all '<=' or '%3C=' to '%3C%3D'
			String[] str1 = s1.split("(<=)|(%3C=)");
			strFinal = "";
			if(str1.length > 1) {
				for(int i = 0; i < str1.length ; i++) {
					strFinal = strFinal + str1[i];
					if( i < str1.length - 1) {	
						strFinal = strFinal + "%3C%3D";
					}
				}
				s1 = strFinal;
			}

            
            StringTokenizer stringtokenizer1 = new StringTokenizer(s1, "=");
            if(stringtokenizer1.hasMoreTokens())
            {
                s2 = stringtokenizer1.nextToken();
            }
            if(stringtokenizer1.hasMoreTokens())
            {
                s3 = stringtokenizer1.nextToken();
            }

            if(s2 != null && s3 != null && !hashmap.containsKey(s2))
            {
                ArrayList arraylist = (ArrayList)hashmap.get(s2);
                if(arraylist == null)
                {
                    arraylist = new ArrayList();
                }
                arraylist.add(s3);
                hashmap.put(s2, arraylist);
            }
        } while(true);
        return hashmap;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        Set set = nameMultiMap.entrySet();
        Iterator iterator = set.iterator();
        boolean flag = true;
        while(iterator.hasNext()) 
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String s = (String)entry.getKey();
            Collection collection = (Collection)entry.getValue();
            Iterator iterator1 = collection.iterator();
            while(iterator1.hasNext()) 
            {
                String s1 = (String)iterator1.next();
                if(flag)
                {
                    flag = false;
                } else
                {
                    stringbuffer.append("&");
                }
                stringbuffer.append(s);
                stringbuffer.append("=");
                stringbuffer.append(s1);
            }
        }
        return stringbuffer.toString();
    }

}
