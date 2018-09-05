function getFormValue(){
  var userName = document.forms.id_form1.userName.value;
  var yomi = document.forms.id_form1.yomi.value;
  var tel = document.forms.id_form1.tel.value;
  var email = document.forms.id_form1.email.value;
  var company = document.forms.id_form1.company.value;
  var position = document.forms.id_form1.position.value;
  var cache = document.forms.id_form1.cache.value;
  var endDay = document.forms.id_form1.endDay.value;
  var robot = document.forms.id_form1.robot.value;
  var plan = document.forms.id_form1.plan.value;
  var style = document.forms.id_form1.id_style.value;
  var character = document.forms.id_form1.character.value;
  var image1 = document.forms.id_form1.image1.value;
  var sentence1 = document.forms.id_form1.sentence1.value;
  var result = "お名前：" + userName + " ヨミガナ：" +　yomi + " 電話番号：" +　tel + " メール：" +　email + " 企業名：" +　company + " 役職：" +　position + " 支払い方法：" +　cache + " 納品希望日：" +　endDay + " ロボット：" +　robot + " プラン：" +　plan + " スタイル：" +　style + " キャラクター：" +　character + " 画像1：" +　image1 + " 文章1：" +　sentence1;
  alert(result);
}
