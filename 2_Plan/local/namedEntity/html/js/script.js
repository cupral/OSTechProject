// サーバーからJSONを取得
var xhr = new XMLHttpRequest();
var xhr2 = new XMLHttpRequest();


// JSON読込
function getNamedEntity(){
  var data = {"app_id":"2660758cb2e394f2d12288063eae7a425404423b3ccd36f92128d57df7f86217", "request_id":"record002", "sentence":"前橋市のスナック銃乱射事件で死刑が確定した矢野治死刑囚（67）が別の男性殺害を告白した事件で、警視庁は30日までに、同死刑囚らの供述通り埼玉県ときがわ町の山中から白骨化した遺体の一部を発見した。〔写真特集〕知られざる塀の向こう側～東京拘置所の「執行室」～　遺体は行方不明となっている不動産会社社長斎藤衛さん＝失踪当時（49）＝の可能性があり、同庁は身元の特定を急ぐとともに、殺人容疑を視野に捜査する。同庁は21、22両日、同町大野の林道脇斜面を捜索したが、遺体は発見されなかった。29日午後1時ごろ、穴を埋め戻していた作業員が捜索現場近くの地中から、大腿（だいたい）骨など人間の白骨化した下半身を発見した。同庁は改めて周辺を検証しており、残りの部位を捜索する。捜査関係者によると、矢野死刑囚は2014年9月、東京拘置所から「（斎藤さんの）首を絞めて殺した」と告白する文書を警視庁目白署に提出。斎藤さんは元参院議員が関与した「オレンジ共済組合事件」に関連して1997年に国会で証人喚問された人物で、98年から行方が分からなくなっているという。失踪直後に殺害、遺棄されたとすると、死体遺棄罪については公訴時効が成立している。矢野死刑囚は他に、96年に失踪していた神奈川県伊勢原市の不動産業の男性についても殺害関与を告白。今年4月、供述通り同市の山中から遺体が発見された。"};
  xhr.addEventListener('loadend', function(){
    // var userList = document.getElementById("userList");
    if(xhr.status === 200){
      // console.log(JSON.stringify(xhr.response));//xhr.response = object;
      // console.log(xhr.response);
      console.log(xhr.response);
      var ne_list = xhr.response["ne_list"];console.log(ne_list);
      var neLen = ne_list.length;console.log(neLen);
      var nameList = "奥野貴則";
      for (var i = 0; i < neLen; i++) {
        if (ne_list[i][1]=="PSN") {
          var name = ne_list[i][0];
          console.log(name)
          nameList += name + ",";
        }else {
          console.log("through");
        }
      }
      getHiragana(nameList);
    }else {
      console.error(xhr.status+" "+xhr.statusText);
    }
  });

  xhr.open( 'POST', 'https://labs.goo.ne.jp/api/entity', true );
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr.responseType = 'json';
  xhr.send( EncodeHTMLForm( data ));
}

function EncodeHTMLForm(data){
  var params = [];
  for(var name in data){
    var value = data[name];
    var param = encodeURIComponent(name) + '=' + encodeURIComponent(value);

    params.push(param);
  }
  return params.join('&').replace(/%20/g,'+');
}

function getHiragana(name){
  var data = {"app_id":"2660758cb2e394f2d12288063eae7a425404423b3ccd36f92128d57df7f86217", "request_id":"record003", "sentence":name,"output_type":"hiragana"};
  xhr2.addEventListener('loadend', function(){
    if (xhr2.status === 200) {
      console.log(JSON.stringify(xhr2.response));
    }
  });

  xhr2.open( 'POST', 'https://labs.goo.ne.jp/api/hiragana', true );
  xhr2.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr2.responseType = 'json';
  xhr2.send( EncodeHTMLForm( data ));
}
