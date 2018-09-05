from datetime import datetime
import time
 
class MyClass(GeneratedClass):
    def __init__(self):
        GeneratedClass.__init__(self)
        #アプリ起動時の時刻を使ってファイル名を生成
        self.filename = datetime.now().strftime("dialog_%Y_%m%d_%H%M_%S.txt")
                                     
    def onLoad(self):
        
        #保存先を指定してファイルオープン、フルパスは下記
        #/home/nao/.local/share/<アプリID>/(self.filename)
        #(Windowsでシミュレータを使ってる場合のパス例, 下記のAppDataフォルダは隠しフォルダな点に注意)
        #(C:\Users\<ユーザ名>\AppData\Roaming\.lastUploadedChoregrapheBehavior\(self.filename))
 
        appId = self.packageUid()
        self.dataPath = qi.path.userWritableDataPath(appId, self.filename)
        self.writer = open(self.dataPath, "w+")
        
        #qi Frameworkをベースにしたモジュールを取得(新しいフレームワーク:こっちが推奨されてる)
        self.mem = ALProxy("ALMemory").session().service("ALMemory")
 
        #subscriber Id
        self.signalHuman = None
        self.signalRobot = None
        self.subHumanId = 0
        self.subRobotId = 0
        
        self.isRunning = False
      
        
    def onUnload(self):
        if self.subHumanId and self.signalHuman:
            self.signalHuman.disconnect(self.subHumanId)
            self.signalHuman = None
            self.subHumanId = 0
            
        if self.subRobotId and self.signalRobot:
            self.signalRobot.disconnect(self.subRobotId)
            self.signalRobot = None
            self.subRobotId = 0
            
        if self.writer != None:
            self.writer.close()
            self.writer = None
    
        self.isRunning = False   
    
    def onInput_onStart(self): 
        if self.isRunning:
            return
            
        self.isRunning = True
 
        if not self.writer:
            self.writer = open(self.dataPath, "w+")
        
        #人の会話
        subscriberHuman = self.mem.subscriber("Dialog/LastInput")
        self.signalHuman = subscriberHuman.signal
        self.subHumanId = self.signalHuman.connect(self.onHumanSpeechDetected)
        
        #Pepperからの発話
        subscriberRobot = self.mem.subscriber("ALTextToSpeech/CurrentSentence")
        self.signalRobot = subscriberRobot.signal
        self.subRobotId = self.signalRobot.connect(self.onRobotSpeechDetected)
 
    
    def onInput_onStop(self):
        self.onUnload()
        self.onStopped()
 
    def onHumanSpeechDetected(self, val):
        self.writeSentenceToFile(val, "Human")
        
    def onRobotSpeechDetected(self, val):
        self.writeSentenceToFile(val, "Robot")
            
    def writeSentenceToFile(self, val, whoTalk):
        if self.writer and val:
            content = "{0}({1}):{2}\n".format(
                whoTalk,
                datetime.now().strftime("%H:%M:%S"),
                val
                )
            self.writer.write(content)
            self.logger.info(content)
            self.writer.flush()