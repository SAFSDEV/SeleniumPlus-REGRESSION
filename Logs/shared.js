function getTestName() {
	var uri = new String(document.location);
	var index = uri.lastIndexOf("/");
	var test = uri.substring(index +1, uri.length);
	var index = test.indexOf(".");
	return test.substring(0, index);
}

/**
 * Fill the content of summary table.
 * @param tables String[], an array of table ID: detailTableID, summaryTableID
 * @param thresholds int[], an array of threshold: warn, alert
 * @param cells String[], an array of cell ID: normalCount, warnCount, alerCount, totalCount, warnLinks, alertLinks
 */
function fillSummaryTable( tables, thresholds, cells){
	var summaryTable = document.getElementById(tables[1]);
	var summaryTableNormalCount = document.getElementById(cells[0]);
	var summaryTableWarnCount = document.getElementById(cells[1]);
	var summaryTableAlertCount = document.getElementById(cells[2]);
	var summaryTableTotalCount = document.getElementById(cells[3]);
	var summaryTableWarnLink = document.getElementById(cells[4]);
	var summaryTableAlertLink = document.getElementById(cells[5]);

	var detailTable = document.getElementById(tables[0]);
	var normalConsumedSeconds = 0;
	var warnConsumedSeconds = 0;
	var alertConsumedSeconds = 0;
	var totalConsumedSeconds = 0;
	var normalcount = 0;
	var warncount = 0;
	var alertcount = 0;
	var count = 0;

	for (var i=1, row, timeColumn, cosumedSecond; row=detailTable.rows[i]; i++) {
		timeColumn = row.cells[3];
		cosumedSecond = parseInt(timeColumn.innerHTML);
		totalConsumedSeconds += cosumedSecond;
		count++;

		if(cosumedSecond>thresholds[1]){
			summaryTableAlertLink.innerHTML += "<a href='#"+timeColumn.id+"'>"+timeColumn.id+"</a> ";
			alertConsumedSeconds += cosumedSecond;
			alertcount++;
		}else if(cosumedSecond>thresholds[0]){
			summaryTableWarnLink.innerHTML += "<a href='#"+timeColumn.id+"'>"+timeColumn.id+"</a> ";
			warnConsumedSeconds += cosumedSecond;
			warncount++;
		}else{
			normalConsumedSeconds += cosumedSecond;
			normalcount++;
		}
	}

	summaryTableNormalCount.innerHTML = normalcount+"/"+normalConsumedSeconds;
	summaryTableAlertCount.innerHTML = alertcount+"/"+alertConsumedSeconds;
	summaryTableWarnCount.innerHTML = warncount+"/"+warnConsumedSeconds;
	summaryTableTotalCount.innerHTML = count+"/"+totalConsumedSeconds;
}