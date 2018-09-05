// =============================================================================
//      Azure_VisionAPI
// =============================================================================
$(function(){
  $('#photo_Check').click(function(){
    touchButtonSE();
    //$(this).text("クリックされました");
    // 要素を取得
    var img = document.getElementById("usr_Photo");
    // Base64 String
    // document.getElementById('log').value = ImageToBase64(img, "image/png");
    var img64 = ImageToBase64(img,"image/png");
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
      ctx.drawImage(image_src, 0, 0);
      // Image Base64
      return canvas.toDataURL(mime_type);
  }
  //=====================================================
  // Base64形式の文字列 → <img>要素に変換
  //   base64img: Base64形式の文字列
  //   callback : 変換後のコールバック。引数は<img>要素
  //=====================================================
  // function Base64ToImage(base64img, callback) {
  //     var img = new Image();
  //     img.onload = function() {
  //         callback(img);
  //     };
  //     img.src = base64img;
  // }

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
    //Request params https://westus.api.cognitive.microsoft.com/vision/v1.0
    //var apiUrl = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze"
    var apiUrl = "https://westus.api.cognitive.microsoft.com/vision/v1.0/analyze"

    var apiKey = "a335af393cf94210a5d5f61273a200de";

    var params = {
      "visualFeatures": "Categories,Tags",
      "details": "Landmarks",
      "language": "en",};
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
        try {
          var tags = data["tags"];
          console.log(tags);
          var tagsLen = tags.length;
          console.log(tagsLen);
          var step;
          var resultName;
          for(step = 0; step < tagsLen; step++){
            resultName += tags[step]["name"]+ ",";
          }
          console.log(resultName);
          sendGGLTrans(resultName);
        } catch (e) {
          tts("say","配列が正しく受信できませんでした。技術者を呼んでください。");
          console.log(e);
        }
        console.log(JSON.stringify(data, null, 2));
      })
      .fail(function(jqXHR, textStatus, errorThrown) {
        // Display error message.
        tts("say","ネットワークの接続状況が悪いか、ビジョンエーピーアイとうまく連動できません。ネットワークの接続状況を確認してください。");
        var errorString = (errorThrown === "") ? "Error. " : errorThrown + " (" + jqXHR.status + "): ";
        errorString += (jqXHR.responseText === "") ? "" : jQuery.parseJSON(jqXHR.responseText).message;
        console.log(errorString);
      });
  };
