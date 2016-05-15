//
//  SAMCProducerManager.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SAMCProducerManager : NSObject

- (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, NSError *error))completion;
- (NSArray *)unresponsedQuestionIdsFrom:(NSString *)username markResponsed:(BOOL)flag;

@end
