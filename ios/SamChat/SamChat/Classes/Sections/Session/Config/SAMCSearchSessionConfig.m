//
//  SAMCSearchSessionConfig.m
//  SamChat
//
//  Created by HJ on 5/8/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSearchSessionConfig.h"
#import "SAMCSearchMessageDataProvider.h"

@interface SAMCSearchSessionConfig ()

@property (nonatomic,strong) SAMCSearchMessageDataProvider *provider;

@end

@implementation SAMCSearchSessionConfig

- (instancetype)initWithSession:(NIMSession *)session
{
    self = [super init];
    if (self) {
        self.provider = [[SAMCSearchMessageDataProvider alloc] initWithSession:session];
        self.session = session;
    }
    return self;
}

- (NSInteger)messageLimit
{
    return 20;
}

- (id<NIMKitMessageProvider>)messageDataProvider
{
    return self.provider;
}

@end
