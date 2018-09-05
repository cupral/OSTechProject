var session = new QiSession();


var fs = new ActiveXObject( "Scripting.FileSystemObject" );
var file = fs.OpenTextFile("text.txt", 2, true, 0);
file.Write("マルペケつくろ～だぜ！");
file.Close();

session.service('ALMemory').done(function(alMemory){
    alMemory.subscriber('WordRecognized').done(function(subscriber){
        subscriber.signal.connect(function(val){
            // イベントが発生すると呼び出される
            console.log('[EVENT]WordRecognized:' + val);
            var file = fs.OpenTextFile("text.txt", 8, true, 0);
            file.Write(val);
            file.Close();
        });
    });
});
