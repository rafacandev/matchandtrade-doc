function openTab(evt, tabId) {
	var i, tabcontent, tablinks;
	var tabIdEnding = tabId.substring(tabId.length - 2);

	tabcontent = document.getElementsByClassName("tabcontent");
	for (i = 0; i < tabcontent.length; i++) {
		if (tabcontent[i].id.endsWith(tabIdEnding)) {
			tabcontent[i].style.display = "none";
		}
	}

	tablinks = document.getElementsByClassName("tablinks");
	for (i = 0; i < tablinks.length; i++) {
		if (tablinks[i].id.endsWith(tabIdEnding)) {
			tablinks[i].className = tablinks[i].className
					.replace(" active", "");
		}
	}

	document.getElementById(tabId).style.display = "block";
	evt.currentTarget.className += " active";
}

function openTabById(tabLinkId, tabId) {
	document.getElementById(tabId).style.display = "block";
	document.getElementById(tabId).className += " active";

	document.getElementById(tabLinkId).className += " active";
}