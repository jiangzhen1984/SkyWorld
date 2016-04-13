//
//  SCArticleModel.m
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleModel.h"



@implementation SCArticleModel

+ (void)publishArticleWithImages:(NSArray *)images comment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    NSUInteger count = [images count];
    if(count > MAX_ARTICLE_IMAGE_COUNT){
        count = MAX_ARTICLE_IMAGE_COUNT;
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];

    [manager POST:[SCSkyWorldAPI urlArticlePublishWithComment:comment]
       parameters:nil
constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
    if(count == 0){
        [formData appendPartWithFormData:[NSData data] name:@"none"];
    }
    for (int i=0; i<count; i++) {
        id image = images[i];
        if([image isKindOfClass:[UIImage class]]){
            //UIImage *scalImage = [SCUtils scalingAndCroppingImage:image ForSize:CGSizeMake(120.f, 120.f)];
            UIImage *scalImage = image;
            NSData *imageData;
//            if (UIImagePNGRepresentation(scalImage)) {
//                imageData = UIImagePNGRepresentation(scalImage);
//            }else {
                imageData = UIImageJPEGRepresentation(scalImage, 1.0); // size smaller
//            }
            [formData appendPartWithFormData:imageData name:[NSString stringWithFormat:@"%d", i]];
        }
    }
} progress:^(NSProgress * _Nonnull uploadProgress) {
} success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
    if([responseObject isKindOfClass:[NSDictionary class]]) {
        DebugLog(@"publish article success:%@", responseObject);
        NSDictionary *response = responseObject;
        NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
        NSArray *pics = [response valueForKeyPath:SKYWORLD_ARTICLE_PICS];
        if((errorCode) || (pics==nil)){
            if(completion){
                completion(false, [SCSkyWorldError errorWithCode:errorCode]);
            }
        }else{
#warning save article to database
            if (completion){
                completion(true, nil);
            }
        }
    }else{
        if(completion){
            completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
        }
    }
} failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
    DebugLog(@"avatar failed:%@", error);
    if (completion) {
        completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
    }
}];

}

+ (void)queryArticleWithTimeFrom:(NSTimeInterval)from to:(NSTimeInterval)to count:(NSInteger)count completion:(void (^)(BOOL success, NSArray *articles, SCSkyWorldError *error))completion
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    NSString *urlString = [SCSkyWorldAPI urlArticleQueryWithTimeFrom:from
                                                                  to:to
                                                               count:count
                                                                type:1]; // 0: for native; 1: for easemob contacts
    DebugLog(@"url: %@", urlString);
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress * _Nonnull downloadProgress) {
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            DebugLog(@"responseObject: %@", responseObject);
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    if(completion){
                        completion(false, nil, [SCSkyWorldError errorWithCode:errorCode]);
                    }
                }else{
                    NSArray *articles = response[SKYWORLD_ARTICLES];
                    if(completion){
                        completion(true, articles, nil);
                    }
                }
            }else{
                if(completion){
                    completion(false, nil, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                }
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            if(completion){
                completion(false, nil, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
            }
        }];
}

@end
