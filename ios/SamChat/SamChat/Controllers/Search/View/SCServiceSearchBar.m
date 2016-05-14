//
//  SCServiceSearchBar.m
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCServiceSearchBar.h"

@interface SCServiceSearchBar ()

@property (nonatomic, strong) UITextField *searchTextField;

@end

@implementation SCServiceSearchBar


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
    
    _searchTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 5, self.frame.size.width-10*2, self.frame.size.height-5*2)];
    _searchTextField.placeholder = @"请输入您的问题或需求";
    _searchTextField.backgroundColor = [UIColor lightGrayColor];
    _searchTextField.font = [UIFont systemFontOfSize:14];
    [_searchTextField addTarget:self.delegate action:@selector(searchEditingDidBegin) forControlEvents:UIControlEventEditingDidBegin];
    [_searchTextField addTarget:self.delegate action:@selector(searchEditingDidEndOnExit) forControlEvents:UIControlEventEditingDidEndOnExit];
    [self addSubview:_searchTextField];
}


- (void)setSearchContent:(NSString *)text
{
    _searchTextField.text = text;
}

- (NSString *)searchContent
{
    return _searchTextField.text;
}

@end
