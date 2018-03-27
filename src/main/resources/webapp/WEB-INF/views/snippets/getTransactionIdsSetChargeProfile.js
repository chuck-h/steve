$("#chargePointSelectList").change(function() {
	var cp = $(this).find("option:selected").text();
	$.getJSON("${ctxPath}/manager/ajax/" + cp + "/transactionIds", function(data) {
		var options = "";
		options += "<option style='color:gray' value='' label='optional' />";
		$.each(data, function() {
			options += "<option value='" + this + "'>" + this + "</option>";
		});
		var select = $("#transactionId");
		select.prop("disabled", false);
		select.html(options);
	});
});
