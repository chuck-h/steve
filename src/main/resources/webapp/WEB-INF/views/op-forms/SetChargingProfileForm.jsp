<form:form action="${ctxPath}/manager/operations/v1.6/SetChargingProfile" modelAttribute="params">
	<section><span>Charge Points with OCPP v1.6</span></section>
	<%@ include file="../00-cp-multiple.jsp" %>
	<section><span>Parameters</span></section>
	<table class="userInput" border="0">
		<tr><td>Connector ID :</td>
			<td><form:input path="connectorId" placeholder="if empty, charge point(s) as a whole" /></td>
		</tr>
		<tr class="header expand" id="cp"><td class="noselect"><b>Charging Profile <span class="sign" /></b></td></tr>
		<tr data-for="cp" style="display:none">
			<td>ID of the Active Transaction:</td>
			<td>
				<form:select path="transactionId" disabled="true" />
			</td>
		</tr>
		<tr data-for="cp" style="display:none"><td>Charging Profile ID (integer):</td><td><form:input path="chargingProfileId" placeholder="" /></td></tr>
		<tr data-for="cp" style="display:none"><td>Stack Level (integer):</td><td><form:input path="stackLevel" placeholder="" /></td></tr>
		<tr data-for="cp" style="display:none">
			<td>Charging Profile Purpose :</td>
			<td>
				<form:select path="chargingProfilePurpose" >
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
		<tr class="header expand" id="cs"><td class="noselect"><b>Charging Schedule <span class="sign" /></b></td></tr>
		<tr data-for="cs" style="display:none"><td>Duration (integer) :</td><td><form:input path="duration" placeholder="optional" /></td></tr>
		<tr data-for="cs" style="display:none"><td>Start Schedule :</td><td><form:input path="startSchedule" cssClass="dateTimePicker" placeholder="optional" /></td></tr>
		<tr data-for="cs" style="display:none">
			<td>Charging Rate Unit :</td>
			<td>
				<form:select path="chargingRateUnit" >
					<form:options items="${chargingRateUnit}" />
				</form:select>
			</td>
		</tr>
		<tr data-for="cs" style="display:none"><td>Minimum Charging Rate (decimal) :</td><td><form:input path="minChargingRate" placeholder="optional" /></td></tr>
		
		<tr class="header expand" id="csp"><td class="noselect"><b>Charging Schedule Period <span class="sign" /></b></td></tr>
		
			<tr data-for="csp" style="display:none"><td>Start Period (integer) :</td><td><form:input path="startPeriod" placeholder="First Start Period SHALL always be 0" /></td></tr>

			<tr data-for="csp" style="display:none"><td>Limit (decimal, multiple of 0.1) :</td><td><form:input path="limit" /></td></tr>

			<tr data-for="csp" style="display:none"><td>Number of Phases (integer) :</td><td><form:input path="numberPhases" placeholder="optional, if empty, 3 will be assumed" /></td></tr>

		<tr>
			<td></td><td><div class="submit-button"><input type="submit" disabled="true" value="Perform"></div></td>
		</tr>
	</table>
	<script>
		$(".header").click(function () {
			$(this).toggleClass('expand').nextUntil('tr.header');
			$("[data-for="+this.id+"]").slideToggle(10);  
		});
	</script>
</form:form>