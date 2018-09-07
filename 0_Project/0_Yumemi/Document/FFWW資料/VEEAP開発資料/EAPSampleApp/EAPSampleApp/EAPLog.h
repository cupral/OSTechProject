//
//  EAPLog.h
//  EAPSampleApp
//
//

#ifndef EAPLog_h
#define EAPLog_h

@class EAPLog;

///////////////////////////////////////////////////////////////////////////
// interface
///////////////////////////////////////////////////////////////////////////
/**
 EAPLog
 */
@interface EAPLog : NSObject {

}

@property (nonatomic, retain) NSMutableArray *handleArray;
@property (nonatomic, strong) NSFileHandle *_handle;


+ (EAPLog *)sharedLogInfo;

- (void) write_appsend:(NSString *)msgkind :(NSString *)json_str;
- (void) write:(NSString *)outputString;
- (void) output_sw:(BOOL)on;
- (NSString *) getTimestamp:(NSString *)format;

@end

#endif /* EAPLog_h */
