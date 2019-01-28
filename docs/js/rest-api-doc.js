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


function onSnippetLabelClick(label) {
    var snippetContents = label.parentElement.parentElement.getElementsByClassName('snippet-content');
    var targetContent = document.getElementById(label.htmlFor);
    for (var i=0; i < snippetContents.length; i++) {
      var content = snippetContents[i];
      content.classList.remove('active');
    }
    targetContent.classList.add('active');
}
