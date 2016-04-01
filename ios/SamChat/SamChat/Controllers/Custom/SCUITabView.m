//
//  SCUITabView.m
//  SamChat
//
//  Created by HJ on 4/1/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUITabView.h"

#define MAP(a, b, c) MIN(MAX(a, b), c)
#define TAB_ITEM_WIDTH      (self.frame.size.width/self.tabViews.count)

@interface SCUITabView ()

- (void)_initTabbatAtIndex:(NSInteger)index;

@property (strong, nonatomic) NSArray *tabViews;
@property (strong, nonatomic) NSLayoutConstraint *tabIndicatorDisplacement;
@property (strong, nonatomic) NSLayoutConstraint *tabIndicatorWidth;

@end

@implementation SCUITabView

#pragma mark - Initialize Methods

- (instancetype)initWithFrame:(CGRect)frame tabViews:(NSArray *)tabViews tabBarHeight:(CGFloat)height tabColor:(UIColor *)color backgroundColor:(UIColor *)backgroundColor selectedTabIndex:(NSInteger)index
{
    self = [self initWithFrame:frame tabViews:tabViews tabBarHeight:height tabColor:color backgroundColor:backgroundColor];
    if (self) {
        NSInteger tabIndex = 0;
        if (index) {
            tabIndex = index;
        }
        [self _initTabbatAtIndex:index];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame tabViews:(NSArray *)tabViews tabBarHeight:(CGFloat)height tabColor:(UIColor *)color backgroundColor:(UIColor *)backgroundColor
{
    self = [super initWithFrame:frame];
    
    if (self) {
        
        self.tabViews = tabViews;
        
        UIView *contentView = [UIView new];
        [contentView setFrame:CGRectMake(0, 0, self.frame.size.width, height)];
        [contentView setBackgroundColor:backgroundColor];
        //[contentView setTranslatesAutoresizingMaskIntoConstraints:NO];
        [self addSubview:contentView];
        
        NSMutableString *VFL = [NSMutableString stringWithString:@"H:|"];
        NSMutableDictionary *views = [NSMutableDictionary dictionary];
        int index = 0;
        
        
        for (UIView *tab in tabViews) {
            [contentView addSubview:tab];
            [tab setTranslatesAutoresizingMaskIntoConstraints:NO];
            [VFL appendFormat:@"-%f-[T%d%@]", 0.0, index, index==0?@"":@"(==T0)"];
            [views setObject:tab forKey:[NSString stringWithFormat:@"T%d", index]];
            
            [contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-0-[T]-0-|"
                                                                                options:0
                                                                                metrics:nil
                                                                                  views:@{@"T": tab}]];
            [tab setTag:index];
            [tab setUserInteractionEnabled:YES];
            [tab addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tabTapHandler:)]];
            
            index++;
        }
        
        [VFL appendString:[NSString stringWithFormat:@"-%f-|", 0.0]];
        
        [contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:VFL
                                                                            options:0
                                                                            metrics:nil
                                                                              views:views]];
        
        // indicator
        UIView *tabIndicator = [UIView new];
        [tabIndicator setTranslatesAutoresizingMaskIntoConstraints:NO];
        [contentView addSubview:tabIndicator];
        [tabIndicator setBackgroundColor:color];
        
        [self setTabIndicatorDisplacement:[NSLayoutConstraint constraintWithItem:tabIndicator
                                                                       attribute:NSLayoutAttributeLeading
                                                                       relatedBy:NSLayoutRelationEqual
                                                                          toItem:contentView
                                                                       attribute:NSLayoutAttributeLeading
                                                                      multiplier:1.0f
                                                                        constant:0]];
        
        [self setTabIndicatorWidth:[NSLayoutConstraint constraintWithItem:tabIndicator
                                                                attribute:NSLayoutAttributeWidth
                                                                relatedBy:NSLayoutRelationEqual
                                                                   toItem:nil
                                                                attribute:0
                                                               multiplier:1.0f
                                                                 constant:[tabViews[0] frame].size.width]];
        
        [contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[S(2)]-0-|"
                                                                            options:0
                                                                            metrics:nil
                                                                              views:@{@"S": tabIndicator}]];
        
        [contentView addConstraints:@[[self tabIndicatorDisplacement], [self tabIndicatorWidth]]];
    }
    
    return self;
}

#pragma mark - Public Methods

- (void)animateToTabAtIndex:(NSInteger)index
{
    [self animateToTabAtIndex:index animated:YES];
}

- (void)animateToTabAtIndex:(NSInteger)index animated:(BOOL)animated
{
    CGFloat animatedDuration = 0.4f;
    if (!animated) {
        animatedDuration = 0.0f;
    }
    
    CGFloat x = TAB_ITEM_WIDTH*index;
    
    [UIView animateWithDuration:animatedDuration
                     animations:^{
                         [[self tabIndicatorDisplacement] setConstant:x];
                         [[self tabIndicatorWidth] setConstant:TAB_ITEM_WIDTH];
                         [self layoutIfNeeded];
                     }];
}

- (void)tabTapHandler:(UITapGestureRecognizer *)gestureRecognizer
{
    if ([[self tabDelegate] respondsToSelector:@selector(tabView:didSelectTabAtIndex:)]) {
        NSInteger index = [[gestureRecognizer view] tag];
        [[self tabDelegate] tabView:self didSelectTabAtIndex:index];
        [self animateToTabAtIndex:index];
    }
}

#pragma mark - Private Methods

- (void)_initTabbatAtIndex:(NSInteger)index
{
    [[self tabIndicatorDisplacement] setConstant:0];
    [[self tabIndicatorWidth] setConstant:TAB_ITEM_WIDTH];
    [self layoutIfNeeded];
}


@end
