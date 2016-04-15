//
//  SCUserRelationModel.h
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCUserRelationModel : NSObject

+ (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

@end
