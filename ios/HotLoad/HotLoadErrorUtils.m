#import "HotLoad.h"

@implementation HotLoadErrorUtils

static NSString *const HotLoadErrorDomain = @"HotLoadError";
static const int HotLoadErrorCode = -1;

+ (NSError *)errorWithMessage:(NSString *)errorMessage
{
    return [NSError errorWithDomain:HotLoadErrorDomain
                               code:HotLoadErrorCode
                           userInfo:@{ NSLocalizedDescriptionKey: NSLocalizedString(errorMessage, nil) }];
}

+ (BOOL)isHotLoadError:(NSError *)err
{
    return err != nil && [HotLoadErrorDomain isEqualToString:err.domain];
}

@end