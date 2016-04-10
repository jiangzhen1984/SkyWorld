//
//  SCAnswerQuestionModel.h
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCAnswerQuestionModel : NSObject

+ (void)sendAnswer:(NSString *)answer toQuestionID:(NSInteger)question_id completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;


@end
