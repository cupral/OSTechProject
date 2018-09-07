//
//  EAPLog.m
//  EAPSampleApp
//
//

#import <Foundation/Foundation.h>
#import "EAPLog.h"

///////////////////////////////////////////////////////////////////////////
// implementation
///////////////////////////////////////////////////////////////////////////
@implementation EAPLog

BOOL log_output_flg = TRUE;

//--------------------------------------------------------------------
#pragma mark - Public Methods
//--------------------------------------------------------------------
+ (EAPLog *)sharedLogInfo
{
    static EAPLog *logFiler = nil;
    
    @synchronized(self) {
        if (logFiler == nil) {
            logFiler = [[EAPLog alloc] init];
        }
    }
    return logFiler;
}

- (void)dealloc
{
    NSFileHandle *handle = self._handle;
    if (handle != nil) {
        [handle closeFile];
    }
    
}

/**
 * write_appsend
 */
- (void)write_appsend:(NSString *)msgkind :(NSString *)json_str
{
    if (log_output_flg) {
        NSFileHandle *handle = self._handle;
        if (handle != nil) {
            NSString *output_str = [NSString stringWithFormat:@"%@ %@ %@ \n", (NSString *)[self getTimestamp:@"MM-dd HH:mm:ss.SSS"], msgkind, json_str];
            
            [handle truncateFileAtOffset:[handle seekToEndOfFile]];
            NSData *data = [output_str dataUsingEncoding:NSUTF8StringEncoding];
            [handle writeData:data];
            [handle synchronizeFile];
        }
    }
}

/**
 * write
 */
- (void)write:(NSString *)outputString
{
    if (log_output_flg) {
        NSFileHandle *handle = self._handle;
        if (handle != nil) {
            NSString *output_str = [NSString stringWithFormat:@"%@ %@\n", (NSString *)[self getTimestamp:@"MM-dd HH:mm:ss.SSS"], (NSString *)outputString];
            
            [handle truncateFileAtOffset:[handle seekToEndOfFile]];
            NSData *data = [output_str dataUsingEncoding:NSUTF8StringEncoding];
            [handle writeData:data];
            [handle synchronizeFile];
        }
    }
}

/**
 * output_sw
 */
- (void)output_sw:(BOOL)on
{
    log_output_flg = on;
    if (log_output_flg) {
        if (self._handle == nil) {
            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            if (paths != nil) {
                NSString *documentsDirectory = [paths objectAtIndex:0];
                NSString *FileName = [NSString stringWithFormat:@"MDEAPTEST_recv_%@.log", (NSString *)[self getTimestamp:@"yyyyMMdd_HHmmss"]];
            
                if ((documentsDirectory != nil) && (FileName != nil)) {
                    NSString *path = [documentsDirectory stringByAppendingPathComponent:FileName];

                    NSFileManager *fileManager = [NSFileManager defaultManager];
                
                    if ((fileManager != nil) && (path != nil)) {
                        BOOL result = [fileManager fileExistsAtPath:path];
                        if(!result){
                            result = [self createFile:path];
                        }
                        if(result){
                            self._handle = [NSFileHandle fileHandleForWritingAtPath:path];
                        }
                    }
                }
            }
        }
    }
    else {
        NSFileHandle *handle = self._handle;
        if (handle != nil) {
            [handle closeFile];
        }
    }
}

/**
 * createFile
 */
- (BOOL)createFile:(NSString *)filePath
{
    return [[NSFileManager defaultManager] createFileAtPath:filePath contents:[NSData data] attributes:nil];
}


/**
 * getTimestamp
 */
- (NSString *)getTimestamp:(NSString *)format
{
    NSDateFormatter* now_date = [[NSDateFormatter alloc] init];
    [now_date setDateFormat:format];
    
    NSString *string = [now_date stringFromDate:[NSDate date]];
    return string;
}

@end
