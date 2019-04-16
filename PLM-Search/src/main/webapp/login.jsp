<%@ page errorPage="error.jsp" %>
<%
	session.setAttribute("showTC", "Y");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<meta http-equiv="pragma-directive" content="no-cache" />
		<meta http-equiv="cache-directive" content="no-cache" />
		<script type="text/javascript" src="media/js/prototype.js"></script>
		<script type="text/javascript" src="media/js/ext-prototype-adapter.js"></script>
		<script type="text/javascript" src="media/js/load.js"></script>
		<link id="b_theme_css" href="media/style/slim_base.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/yaml_basemod.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/brasato.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/olat.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/content.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/slim_print_base.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/yaml_basemod_print.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/brasato_print.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/olat_print.css" rel="stylesheet" type="text/css" />
		<link id="b_theme_css" href="media/style/ext-all.css" rel="stylesheet" type="text/css" />
		<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<title>Parole LEADS 2.0</title>
	</head>
	<body>
		<div align="center">
			<div id="Maindiv">
				<div id="Container" >
					<div id="Header">
						<div id="logoimg">
							<a href="welcome_screen_rd.html">
								<img src="media/images/global/logo.gif" alt="Men's Wearhouse" border="0"/>
							</a>  
						</div>
						<div id="topglobalnav">
<!-- Header Part Start -->
							<div id="Top">
								<div>
									<div class="headerNav">
										<div id="paroleesearch_text"><img src="media/images/global/paroleesearch_text.gif" alt="" /></div>
									</div>
								</div>
							</div>
							<div id="GlobalNav">
								<div>
								</div>
							</div>
						</div>
					</div>
<!-- Header Part End -->
<!-- Middle Part Start -->
					<div id="Middle"  class="content_box" >
						<div  id="middletop">
							<a class="b_dev" id="o_lnk1000000401" href="/olat/dmz/1%3A2%3A1000000401%3A2%3A0%3Acid%3AdevTool/" >
								<span  title="Development tool"></span>
							</a>
							<div id="b_page_margins">
								<div id="b_page_wrapper">
									<a id='b_top' name="b_top"></a>
									<span class="b_skip">Access keys for a quick navigation</span>
									<a accesskey="0" class="b_skip" href="#b_top">0: Top of page</a>
									<a accesskey="n" class="b_skip" href="#b_nav">n: Main navigation</a>
									<span class="b_skip">Elements of main navigation: h: home; g; groups; r: learning resources</span>
									<span class="b_skip">Open resources and groups: 1, 2, 3...</span>
									<a accesskey="o" class="b_skip" href="#b_topnav">o: Top navigation</a>
									<a accesskey="m" class="b_skip" href="#b_col1">m: Menu</a>
									<a accesskey="c" class="b_skip" href="#b_col3">c: Content area</a>
									<a accesskey="t" class="b_skip" href="#b_col2">w: Tools and actions</a>
									<a accesskey="a" class="b_skip" href="#b_table">a: Table content (if available)</a>
									<a accesskey="d" class="b_skip" href="#b_modal">d: Modal dialog (if available)</a>
									<span class="b_skip">b: One step back (if available)</span>
									<div id="b_nav">
										<a name="b_nav"></a>
										<div id="b_nav_main">
										</div>
									</div>
									<div id="b_main" class="o_loginscreen b_hidecol2">
										<div id="b_col3" style="margin-left: 14em; margin-right: 12em;">
											<div id="b_col3_content" class="b_clearfix">
												<a id="b_content" name="content"></a>
												<div id="b_col3_content_inner" class="b_floatscrollbox">
													<a name="b_col3"></a>
													<div class="o_login">
														<h3>Parole LEADS 2.0</h3>
														<div class="o_infomessage_wrapper">
														</div>
														<div class="o_login_form">
															<fieldset>
																<legend>Parole LEADS 2.0 Login</legend>
																<p>
																	Please log in with your personal Parole LEADS 2.0 UserName and Password.
																</p>
																<div class="b_form">
																	<form method="get" name="loginForm" action="plmredirect" id="bfo_13115887">
																		<div class="b_form_element_wrapper b_clearfix" id="ber_lf_loginloginForm">
																			<div class="b_form_element_label ">
																				<label for="bel_19654881">User Name:</label>
																			</div>
																			<div class="b_form_element">
																				<input type="text" id="user_id" name="user_id" value="" size="20" maxlength="128" id="bel_19654881" />
																			</div>
																		</div>
																		<div class="b_form_element_wrapper b_clearfix" id="ber_lf_passloginForm">
																			<div class="b_form_element_label "><label for="bel_29757300">Group:</label></div>
																			<div class="b_form_element"><input name="group" size="20" id="group" maxlength="50"  /></div>
																		</div>
																		<div class="b_form_element_wrapper b_clearfix">
																			<div class="b_form_element">
																				<div class="b_button_group">
																					<input type="submit" name="olat_fosm_0" class="b_button" value="Login" />
																				</div>
																			</div>
																		</div>
																		<script type="text/javascript">
																			/* <![CDATA[ */
																			function checkformloginForm(){
																			}
																			/* ]]> */
																		</script>
																	</form>
																</div>
															</fieldset>
														</div>
													</div>
												</div>
											</div>
											<div id="b_ie_clearing">&nbsp;</div>
										</div>
									</div>
									<div id="b_footer" class="b_clearfix">
									</div>
								</div>
							</div>
							<div id="b_border-bottom">
								<div id="b_edge-lb"></div>
								<div id="b_edge-rb"></div>
							</div>
						</div>
					</div>
					<div id="b_messages">
						<!-- empty -->
					</div>
					<!-- empty -->
<!-- END olatContentPanel -->
					<div id="b_ajax_busy" class=""></div>
					<div id="b_width_1em" style="width:1em; position: absolute; left: -1000px;"></div>
					<div>
					</div>
					<div id="Footer" >
					</div>
				</div>
<!-- Middle Part End -->
<!-- Footer Part Start -->
<!-- Footer Part End -->
			</div>
		</div>
	</body>
</html>
