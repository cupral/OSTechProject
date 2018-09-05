# webapiboxlibrary
Web API を使うような Pepper アプリを作りたい時に役に立つ Box ライブラリ

- Simple HTTP request ボックス ・・・　HTTP リクエストを発行するボックス。結果はボックスの出力 onStopped に出力されます。リクエスト URL はプロパティーで設定
- Upload file ボックス　・・・　HTTP リクエストを発行し、プロパティーで指定されたファイルをリクエスト URL に送るボックス。 サーバーからのレスポンスはボックスの出力 output に出力されます。
- Download File ボックス ・・・　HTTP リクエストを発行し、サーバーからのレスポンスを、プロパティーで指定されたファイルに保存するボックス。保存されたファイルのパスはボックスの onStopped 出力に出力されます。
- WebSocket ボックス　・・・ WebSocket 接続を行うボックス。プロパティー Server URI に接続先の URI を ws:// の形式で設定します。 onStart 入力呼び出しによりサーバーとの接続が確立されます。 sendMessage 入力に文字列を送ることで、サーバーにメッセージを送信、onMessage 出力はサーバーからのメッセージを出力します。

セットアップの仕方、その他詳細は次を参考にしてください。

http://qiita.com/tkawata1025/items/df181d22e44f9150a01d
