#import "HotLoad.h"
#import "RCTConvert.h"

// Extending the RCTConvert class allows the React Native
// bridge to handle args of type "HotLoadInstallMode"
@implementation RCTConvert (HotLoadInstallMode)

RCT_ENUM_CONVERTER(HotLoadInstallMode, (@{ @"hotLoadInstallModeImmediate": @(HotLoadInstallModeImmediate),
                                            @"hotLoadInstallModeOnNextRestart": @(HotLoadInstallModeOnNextRestart),
                                            @"hotLoadInstallModeOnNextResume": @(HotLoadInstallModeOnNextResume) }),
                   HotLoadInstallModeImmediate, // Default enum value
                   integerValue)

@end