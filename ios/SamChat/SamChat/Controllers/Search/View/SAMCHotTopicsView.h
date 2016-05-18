//
//  SAMCHotTopicsView.h
//  SamChat
//
//  Created by HJ on 5/18/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SAMCHotTopicsDelegete <NSObject>

- (void)didSelectHotTopic:(NSString *)topicContent;

@end

@interface SAMCHotTopicsView : UIView

@property (weak, nonatomic) id<SAMCHotTopicsDelegete> delegate;

@end
