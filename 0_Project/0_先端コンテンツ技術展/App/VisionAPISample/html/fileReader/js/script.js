$(function(){
  $('#test').click(function(){
    //$(this).text("クリックされました");
    var img = document.getElementById("MyImg");
    // Base64 String
    document.getElementById('log').value = ImageToBase64(img, "image/png");
    var img64 = ImageToBase64(img,"image/png");
    // var imgBinary  = makeblob(img64);
    sendBinary(img64);
  });

// ===========================================================================
    function ImageToBase64(image_src, mime_type) {
        // New Canvas
        var canvas = document.createElement('canvas');
        canvas.width = image_src.width;
        canvas.height = image_src.height;
        // Draw
        var ctx = canvas.getContext('2d');
        // ctx.drawImage(image_src, 0, 0);
        // Image Base64
        return canvas.toDataURL(mime_type);
    }
// ===========================================================================
  function makeblob(dataURL){
    var BASE64_MARKER = ';base64,';
    if (dataURL.indexOf(BASE64_MARKER) == -1) {
      var parts = dataURL.split(',');
      var contentType = parts[0].split(':')[1];
      var raw = decodeURIComponent(parts[1]);
      return new Blob([raw], { type: contentType });
    }
    var parts = dataURL.split(BASE64_MARKER);
    var contentType = parts[0].split(':')[1];
    var raw = window.atob(parts[1]);
    var rawLength = raw.length;

    var uInt8Array = new Uint8Array(rawLength);

    for (var i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }
    return new Blob([uInt8Array], { type: contentType });
  }
// ===========================================================================

  function sendBinary(file){
    //Request params
    var apiUrl = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze"
    var apiKey = "35d9db25906e48969f7947fcd9b93704";
    var params = {"visualFeatures": "Categories,Tags","details": "Landmarks","language": "en",};
    $.ajax({
      url: apiUrl + "?" + $.param(params),
      beforeSend: function(xhrObj){
          // Request headers
          xhrObj.setRequestHeader("Content-Type","application/octet-stream");
          xhrObj.setRequestHeader("Ocp-Apim-Subscription-Key", apiKey);
      },
      type: "POST",
      data: makeblob(file),
      processData: false
    })
    .done(function(data) {
      // Show formatted JSON on webpage.
      $("#responseTextArea").val(JSON.stringify(data, null, 2));
      var categories = data["categories"];
      console.log(categories);
      var name0 = categories[0]["name"];
      var score0 = categories[0]["score"];
      var name1 = categories[1]["name"];
      var score1 = categories[1]["score"];
      var name2 = categories[2]["name"];
      var score2 = categories[2]["score"];
      if (score0 > score1) {
        if (score0 > score2) {
          sendGGLTrans(name0);
        }
      }else if (score1 > score0) {
        if (score1 > score2) {
          sendGGLTrans(name1);
        }
      }else if (score2 > score0) {
        if (score2 > score1) {
          sendGGLTrans(name2);
        }
      }else {
        alert("scoreIfError");
      }
      console.log(name0);
      console.log(score0);
      console.log(name1);
      console.log(score1);
      console.log(name2);
      console.log(score2);

      console.log(JSON.stringify(data, null, 2));
    })
    .fail(function(jqXHR, textStatus, errorThrown) {
      // Display error message.
      var errorString = (errorThrown === "") ? "Error. " : errorThrown + " (" + jqXHR.status + "): ";
      errorString += (jqXHR.responseText === "") ? "" : jQuery.parseJSON(jqXHR.responseText).message;
      alert(errorString);
    });
  };
// ===========================================================================
});
function sendGGLTrans(category){
  var defURL = 'https://www.googleapis.com/language/translate/v2?key=';
  var apiKey = 'AIzaSyDc0Dd7N3iT0hVYIKgZ8c4u6V4ObEc3yPg';
  var target = '&target=';
  var changelang = 'ja';
  var requestWord = '&q=';
  var changeWord = category;
  var sendUrl = defURL + apiKey + target + changelang + requestWord + changeWord;

  var xhr = $.ajax({
      type: 'GET',
      url: sendUrl,
      dataType: 'jsonp',
      timeout: 30000
  })
  .done(function(res){
      $('body').append('通信成功');
      console.log(res);
      var data = res["data"];
      console.log(data);
      var translations = data["translations"];
      var translatedTxt = translations[0].translatedText;
      console.log(translations[0].translatedText);
  })
  .fail(function(res){
      $('body').append('通信失敗');
  })
  .always(function(res){

  });
}
