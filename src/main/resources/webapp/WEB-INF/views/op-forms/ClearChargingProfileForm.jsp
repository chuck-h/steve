<form:form action="${ctxPath}/manager/operations/${opVersion}/ClearChargingProfile" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
		<tr>
			<td>Charging Profile ID (integer):</td>
			<td><form:input path="id" placeholder="optional" /></td>
		</tr>
		<tr>
			<td>Connector ID (integer):</td><td><form:input path="connectorId" placeholder="optional" /></td>
		</tr>
		<tr>
			<td>Charging Profile purpose :</td>
			<td>
				<form:select path="chargingProfilePurpose" >
					<form:option style="color:gray" value="" label="optional"/>
                    <form:options items="${chargingProfilePurpose}" />
                </form:select>
            </td>
		</tr>
		<tr>
			<td>Stack Level (integer):</td><td><form:input path="stackLevel" placeholder="optional" /></td>
		</tr>
        <tr>
            <td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>