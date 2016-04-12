//
//  SCArticleModel.h
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCArticleModel : NSObject

+ (void)publishArticleWithImages:(NSArray *)images comment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

@end
