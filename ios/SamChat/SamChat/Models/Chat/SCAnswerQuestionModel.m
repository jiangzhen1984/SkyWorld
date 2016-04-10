//
//  SCAnswerQuestionModel.m
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCAnswerQuestionModel.h"

@implementation SCAnswerQuestionModel

+ (void)sendAnswer:(NSString *)answer toQuestionID:(NSInteger)question_id completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    if((answer==nil) || (answer.length<=0)){
        if(completion){
            completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
        }
    }
    NSString *urlString = [SCSkyWorldAPI urlAnswerQuestion:question_id withAnswer:answer];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 //DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     if(completion){
                         completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                     }
                 }else{
                     if(completion){
                         completion(true, nil);
                     }
                 }
             }else{
                 if(completion){
                     //completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                     completion(true, nil);
                 }
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             if(completion){
                 completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
             }
         }];

}


@end
