<form:form action="${ctxPath}/manager/operations/${opVersion}/RemoteStartTransaction" modelAttribute="params">
	<section><span>Charge Points with OCPP ${opVersion}</span></section>
	<%@ include file="../00-cp-single.jsp" %>
	<section><span>Parameters</span></section>
	<table class="userInput">
		<tr>
			<td>Connector ID:</td>
			<td>
				<form:select path="connectorId" disabled="true"/>
			</td>
		</tr>
		<tr>
			<td>OCPP ID Tag:</td>
			<td>
				<form:select path="idTag">
					<form:options items="${idTagList}" />
				</form:select>
			</td>
		</tr>
		<tr id="ChargingProfile" class="header expand" style="display:none;" >
			<td id="chargp" class="noselect"><b>Charging Profile (optional) <span class="sign" /></b></td>
			<td id="chargp2">
				<form:checkbox id="checkb" path="useChargingProfile" onclick="this.checked=!this.checked;" />
			</td>
		</tr>
		<tr data-for="ChargingProfile" style="display:none">
			<td colspan="2" class="noselect"><i><b>Info:</b> Multiple Charging Schedule Periods can be set by separating the values with a ","</i></td>
		</tr>
		<tr data-for="ChargingProfile" style="display:none" class="header expand" id="cp">
			<td class="noselect"><b>Charging Profile <span class="sign" /></b></td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Charging Profile ID (integer):</td>
			<td>
				<form:input path="chargingProfileId" placeholder="" />
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Stack Level (integer):</td>
			<td>
				<form:input path="stackLevel" placeholder="" />
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Charging Profile Purpose :</td>
			<td>
				<form:select path="chargingProfilePurpose" disabled="true" >
					<form:option value="TxProfile" label="TxProfile" />
					<form:options items="${chargingProfilePurpose}" />
				</form:select>
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Charging Profile Kind :</td>
			<td>
				<form:select path="chargingProfileKind" >
					<form:options items="${chargingProfileKind}" />
				</form:select>
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Recurrency Kind :</td>
			<td>
				<form:select path="recurrencyKind" >
					<form:option style="color:gray" value="" label="optional"/>
					<form:options items="${recurrencyKind}" />
				</form:select>
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Valid From :</td>
			<td>
				<form:input path="validFrom" cssClass="dateTimePicker" placeholder="optional" />
			</td>
		</tr>
		<tr data-for="cp" style="display:none">
			<td>Valid To :</td>
			<td>
				<form:input path="validTo" cssClass="dateTimePicker" placeholder="optional" />
			</td>
		</tr>
		<tr data-for="ChargingProfile" style="display:none" class="header expand" id="cs">
			<td class="noselect"><b>Charging Schedule <span class="sign" /></b></td>
		</tr>
		<tr data-for="cs" style="display:none">
			<td>Duration (integer) :</td>
			<td>
				<form:input path="duration" placeholder="optional" />
			</td>
		</tr>
		<tr data-for="cs" style="display:none">
			<td>Start Schedule :</td>
			<td>
				<form:input path="startSchedule" cssClass="dateTimePicker" placeholder="optional" />
			</td>
		</tr>
		<tr data-for="cs" style="display:none">
			<td>Charging Rate Unit :</td>
			<td>
				<form:select path="chargingRateUnit" >
					<form:options items="${chargingRateUnit}" />
				</form:select>
			</td>
		</tr>
		<tr data-for="cs" style="display:none">
			<td>Minimum Charging Rate (decimal) :</td>
			<td>
				<form:input path="minChargingRate" placeholder="optional" />
			</td>
		</tr>
		<tr data-for="ChargingProfile" style="display:none" class="header expand" id="csp">
			<td class="noselect"><b>Charging Schedule Period <span class="sign" /></b></td>
		</tr>
		<tr data-for="csp" style="display:none">
			<td>Start Period (integer) :</td>
			<td>
				<form:input path="startPeriod" placeholder="First Start Period SHALL always be 0" />
			</td>
		</tr>
		<tr data-for="csp" style="display:none">
			<td>Limit (decimal, multiple of 0.1) :</td>
			<td>
				<form:input path="limit" />
			</td>
		</tr>
		<tr data-for="csp" style="display:none">
			<td>Number of Phases (integer) :</td>
			<td>
				<form:input path="numberPhases" placeholder="optional, if empty, 3 will be assumed" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<div class="submit-button"><input type="submit" value="Perform"></div>
			</td>
		</tr>
	</table>
	<script>
		var cp = true; //true = "+", false = "-"
		var cs = true; //true = "+", false = "-"
		var csp = true; //true = "+", false = "-"
		 
		$(".header").click(function () {
			$(this).toggleClass('expand').nextUntil('tr.header');
			$("[data-for="+this.id+"]").slideToggle(10);
			
			if (this.id == "cp") {
				cp = cp ? false : true;
			} if (this.id == "cs") {
				cs = cs ? false : true;
			} if (this.id == "csp") {
				csp = csp ? false : true;
			}
		});
		
		$('#chargp, #chargp2').click(function () {
			var inputs = document.getElementById("checkb");
			
			if (inputs.checked == false) {
				inputs.checked = true;
			} else if (inputs.checked == true) {
				inputs.checked = false; 
				
				if(!cp) {
					$("#cp").toggleClass('expand').nextUntil('tr.header');
					cp = true;
				} if(!cs) {
					$("#cs").toggleClass('expand').nextUntil('tr.header');
					cs = true;
				} if(!csp) {
					$("#csp").toggleClass('expand').nextUntil('tr.header');
					csp = true;
				}
				
				$("[data-for="+document.getElementById("cp").id+"]").slideUp(10);
				$("[data-for="+document.getElementById("cs").id+"]").slideUp(10);
				$("[data-for="+document.getElementById("csp").id+"]").slideUp(10);
				
			}
		});
		
		var show = document.getElementById("ChargingProfile");
		var version = '${opVersion}';
		if (version == 'v1.6') {
		  show.style.display = 'table-row';
		}
	</script>
</form:form>