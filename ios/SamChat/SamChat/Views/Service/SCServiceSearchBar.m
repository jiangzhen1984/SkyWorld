//
//  SCServiceSearchBar.m
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCServiceSearchBar.h"



@implementation SCServiceSearchBar

@synthesize searchContent = _searchContent;

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if(self){
        [self _setupViews];
    }
    return self;
}

- (void)_setupViews
{
    self.backgroundColor = [UIColor yellowColor];
    
    UITextField *searchText = [[UITextField alloc] initWithFrame:CGRectMake(10, 5, self.frame.size.width-10*2, self.frame.size.height-5*2)];
    searchText.placeholder = @"请输入您的问题或需求";
    searchText.backgroundColor = [UIColor lightGrayColor];
    searchText.font = [UIFont systemFontOfSize:14];
    [searchText addTarget:self.delegate action:@selector(searchEditingDidBegin) forControlEvents:UIControlEventEditingDidBegin];
    [searchText addTarget:self.delegate action:@selector(searchEditingDidEndOnExit) forControlEvents:UIControlEventEditingDidEndOnExit];
    [self addSubview:searchText];
}

@end
