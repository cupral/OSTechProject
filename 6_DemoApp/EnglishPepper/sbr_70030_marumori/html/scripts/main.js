$(function() {
  $.subscribeToALMemoryEvent("showJasracNo", function (data) {
    document.getElementById('jasrac_no').innerHTML = decodeURIComponent(data);
    document.getElementById('jasrac_no').style.visibility="visible";
  });

  $.subscribeToALMemoryEvent("hideJasracNo", function (data) {
    document.getElementById('jasrac_no').style.visibility="hidden";
  });

  $.raiseALMemoryEvent("Dance/StartApp","OK");
});