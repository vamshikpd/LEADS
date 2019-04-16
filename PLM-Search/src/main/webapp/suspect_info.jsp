<div id="suspectinfodiv">
	<div id="suspectinfodivtoppart"><img src="media/images/parolee_details/suspect_info_div_top_part.gif" alt="" /></div>
	<div id="suspectinfodivmiddlepart">
		<div>
			<div class="photo">
				<img src="media/images/parolee_details/red_border_photo_frame_image.gif" alt="" />
			</div>
			<div class="frame">
				<img src="media/images/parolee_details/red_border_photo_frame.gif" alt="" />
			</div>
		</div>
	</div>
	<div id="suspectinfodivbottompart"><img src="media/images/global/step1divbottompart.gif" alt="" /></div>
	<div class="suspectinfodivinfo">
		View Photos<br />
		<%=sFstNm%>&nbsp;<%=sLstNm%><br />
		<%=sRace%>,&nbsp;
		<%=sSx%><br />
		<%
		String hei = null;
		if (sHghtFt!=null){
		 hei = sHghtFt + "'" + sHghtInch + "&#8221;";
		}else{
		 hei = "N/A";
		}
		%>
		Height:<%=hei%><br />Weight:<%=sWght%><br />
	</div>
</div>