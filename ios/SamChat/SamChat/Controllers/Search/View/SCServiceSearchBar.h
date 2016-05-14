//
//  SCServiceSearchBar.h
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SCServiceSearchBarDelegate <NSObject>

- (void)searchEditingDidBegin;
- (void)searchEditingDidEndOnExit;

@end

@interface SCServiceSearchBar : UIView

@property (nonatomic, copy) NSString *searchContent;
@property (nonatomic, weak) id<SCServiceSearchBarDelegate> delegate;

@end
