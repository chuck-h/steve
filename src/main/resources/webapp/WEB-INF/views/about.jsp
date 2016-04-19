<%@ include file="00-header.jsp" %>
<div class="content">
<section><span>About SteVe</span></section>
    <c:if test="${releaseReport.moreRecent}">
        <div class="info">A new version (${releaseReport.githubVersion}) is available!
            <a target="_blank" href="${releaseReport.htmlUrl}">Release Info</a> -
            <a target="_blank" href="${releaseReport.downloadUrl}">Download</a>
        </div>
    </c:if>
<table class="userInputFullPage">
	<tr><td>Version:</td><td>${version}</td></tr>
	<tr><td>Database Version:</td><td>${db.version}</td></tr>
	<tr><td>Last Database Update:</td><td>${db.updateTimestamp}</td></tr>
    <tr><td>Log File:</td><td>${logFile}</td></tr>
	<tr><td>GitHub Page:</td><td><a href="https://github.com/RWTH-i5-IDSG/steve">https://github.com/RWTH-i5-IDSG/steve</a></td></tr>
    <tr><td>System Time:</td><td>${systemTime}</td></tr>
    <tr><td>System Time Zone:</td><td>${systemTimeZone}</td></tr>
</table>
</div>
<%@ include file="00-footer.jsp" %>