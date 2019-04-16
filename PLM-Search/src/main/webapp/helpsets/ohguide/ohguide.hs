<?xml version="1.0" encoding="ISO-8859-1" ?>
<helpset version="1.1">
  <title>Parole LEADS 2.0 Help Guide</title>
  <maps>
    <mapref location="map.xml" />
  </maps>

  <links>
    <linkref location="link.xml"/>
  </links>

  <view>
    <label>Contents</label>
    <type>oracle.help.navigator.tocNavigator.TOCNavigator</type>
    <data engine="oracle.help.engine.XMLTOCEngine">toc.xml</data>
  </view>
<!--
  <view>
    <label>Index</label>
    <type>oracle.help.navigator.keywordNavigator.KeywordNavigator</type>    
    <data engine="oracle.help.engine.XMLIndexEngine">index.xml</data>
  </view>
-->
  <view>
    <label>Search</label>
    <title>Parole LEADS 2.0 Help Guide</title>
    <type>oracle.help.navigator.searchNavigator.SearchNavigator</type>
    <data engine="oracle.help.engine.SearchEngine">plmSearch.idx</data>
  </view>
</helpset>
