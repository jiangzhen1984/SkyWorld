//
//  SCArticleCellOperationMenu.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SCArticleCellOperationMenu : UIView

@property (nonatomic, assign, getter = isShowing) BOOL show;

@property (nonatomic, copy) void (^likeButtonClickedOperation)();
@property (nonatomic, copy) void (^commentButtonClickedOperation)();

@end
