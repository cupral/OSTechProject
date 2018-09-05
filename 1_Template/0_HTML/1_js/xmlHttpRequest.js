// http通信 GET JSON処理
var xhr = new XMLHttpRequest();
var step;
var resultArray;

// JSON読込
function getJSON(){
  xhr.addEventListener('loadend', function(){
    var userList = document.getElementById("userList");
    if(xhr.status === 200){
      console.log(JSON.stringify(xhr.response));
      var responseLen = this.response.length;
      var totalJson = scriptJSON(this.response, responseLen);

      //ユーザの要望を数値化
      // var reqGender = document.forms.check_form.gender_select.value; console.log("reqGender:" + reqGender); //

      // ユーザ情報のJSONを取得
      var usrHtml;
      for (var i = 0; i < totalJson.length; i++) {
        var usrJson = totalJson[i];
        var usrName = usrJson[0]; console.log("usrName:" + usrName); //
        var usrGender = usrJson[1]; console.log("usrGender:" + usrGender); //
        var usrAge = usrJson[2]; console.log("usrAge:" + usrAge); //
        var usrAgeLevel = usrAgeLevelJudge(usrAge); console.log("usrAgeLevel:" + usrAgeLevel); //
        var usrId = i; console.log("usrId:" + usrId); //
      }
    }else {
      console.error(xhr.status+" "+xhr.statusText);
    }
  });
  xhr.open("GET","https://private-5c1be-watsonapi1.apiary-mock.com/list", true);
  xhr.responseType = "json";
  xhr.send(null);
}
