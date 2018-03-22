<form:form action="${ctxPath}/manager/operations/${opVersion}/GetCompositeSchedule" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Connector ID (integer):</td>
            <td><form:input path="connectorId" placeholder="if empty, charge point as a whole" /></td>
        </tr>
        <tr>
            <td>Duration (integer):</td>
            <td><form:input path="duration" /></td>
        </tr>
        <tr>
            <td>Charging Rate Unit :</td>
            <td>
                <form:select path="chargingRateUnit">
                    <form:option style="color:gray" value="" label="optional" />
                    <form:options items="${chargingRateUnit}"/>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>