//
//  SCProducerModel.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCProducerModel : NSObject

+ (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;


@end
