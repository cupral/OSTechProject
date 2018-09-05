function conversion(){
  var input = document.forms.form1.input_text.value;
  input = input.replace(/〜/g,"");
  input = input.replace(/。/g,"。\\pau=700\\");
  input = input.replace(/ /g,"、");
  input = input.replace(/&nbsp;/g,"、");
  input = input.replace(/　/g,"、");
  input = input.replace(/「/g,"、");
  input = input.replace(/」/g,"、");
  input = input.replace(/\(/g,"、");
  input = input.replace(/\)/g,"、");
  input = input.replace(/（/g,"、");
  input = input.replace(/）/g,"、");
  input = input.replace(/\n/g,"\\pau=300\\");

  var search1 = document.forms.form2.search1.value;
  var substitution1 = document.forms.form2.substitution1.value;
  var result1 = new RegExp(search1, "g");
  input = input.replace(result1, substitution1);

  var search2 = document.forms.form2.search2.value;
  var substitution2 = document.forms.form2.substitution2.value;
  var result2 = new RegExp(search2, "g");
  input = input.replace(result2, substitution2);

  var search3 = document.forms.form2.search3.value;
  var substitution3 = document.forms.form2.substitution3.value;
  var result3 = new RegExp(search3, "g");
  input = input.replace(result3, substitution3);

  var search4 = document.forms.form2.search4.value;
  var substitution4 = document.forms.form2.substitution4.value;
  var result4 = new RegExp(search4, "g");
  input = input.replace(result4, substitution4);

  var search5 = document.forms.form2.search5.value;
  var substitution5 = document.forms.form2.substitution5.value;
  var result5 = new RegExp(search5, "g");
  input = input.replace(result5, substitution5);



  var output = document.getElementById("output_text");
  output.innerHTML = input;
}
