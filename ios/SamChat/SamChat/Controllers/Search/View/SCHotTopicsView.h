//
//  SCHotTopicsView.h
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SCRefreshFooter.h"
#import "SCRefreshHeader.h"

@protocol SCHotTopicsDelegete <NSObject>

- (void)didSelectHotTopic:(NSString *)topicContent;

@end

@interface SCHotTopicsView : UIView

@property (weak, nonatomic) id<SCHotTopicsDelegete> delegate;

@end
