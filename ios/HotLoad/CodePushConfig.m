#import "HotLoad.h"
#import <UIKit/UIKit.h>

@implementation HotLoadConfig {
    NSMutableDictionary *_configDictionary;
}

static HotLoadConfig *_currentConfig;

static NSString * const AppVersionConfigKey = @"appVersion";
static NSString * const BuildVdersionConfigKey = @"buildVersion";
static NSString * const ClientUniqueIDConfigKey = @"clientUniqueId";
static NSString * const DeploymentKeyConfigKey = @"deploymentKey";
static NSString * const ServerURLConfigKey = @"serverUrl";

+ (instancetype)current
{
    return _currentConfig;
}

+ (void)initialize
{
    _currentConfig = [[HotLoadConfig alloc] init];
}

- (instancetype)init
{
    self = [super init];
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    
    NSString *appVersion = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    NSString *buildVersion = [infoDictionary objectForKey:(NSString *)kCFBundleVersionKey];
    NSString *deploymentKey = [infoDictionary objectForKey:@"HotLoadDeploymentKey"];
    NSString *serverURL = [infoDictionary objectForKey:@"HotLoadServerURL"];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *clientUniqueId = [userDefaults stringForKey:ClientUniqueIDConfigKey];
    if (clientUniqueId == nil) {
        clientUniqueId = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
        [userDefaults setObject:clientUniqueId forKey:ClientUniqueIDConfigKey];
        [userDefaults synchronize];
    }
    
    if (!serverURL) {
        serverURL = @"http://10.10.10.136:9527/";
    }
    
    _configDictionary = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
                            appVersion,AppVersionConfigKey,
                            buildVersion,BuildVdersionConfigKey,
                            serverURL,ServerURLConfigKey,
                            clientUniqueId,ClientUniqueIDConfigKey,
                            deploymentKey,DeploymentKeyConfigKey,
                            nil];
    
    return self;
}

- (NSString *)appVersion
{
    return [_configDictionary objectForKey:AppVersionConfigKey];
}

- (NSString *)buildVersion
{
    return [_configDictionary objectForKey:BuildVdersionConfigKey];
}

- (NSDictionary *)configuration
{
    return _configDictionary;
}

- (NSString *)deploymentKey
{
    return [_configDictionary objectForKey:DeploymentKeyConfigKey];
}

- (NSString *)serverURL
{
    return [_configDictionary objectForKey:ServerURLConfigKey];
}

- (NSString *)clientUniqueId
{
    return [_configDictionary objectForKey:ClientUniqueIDConfigKey];
}

- (void)setDeploymentKey:(NSString *)deploymentKey
{
    [_configDictionary setValue:deploymentKey forKey:DeploymentKeyConfigKey];
}

- (void)setServerURL:(NSString *)serverURL
{
    [_configDictionary setValue:serverURL forKey:ServerURLConfigKey];
}

@end