//
//  SAMCServiceSearchResultController.m
//  SamChat
//
//  Created by HJ on 5/17/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCServiceSearchResultController.h"

@interface SAMCServiceSearchResultController()<UITableViewDataSource,UITableViewDelegate>

@end

@implementation SAMCServiceSearchResultController

- (instancetype)initWithSearchBar:(UISearchBar *)searchBar contentsController:(UIViewController *)viewController
{
    self = [super initWithSearchBar:searchBar contentsController:viewController];
    if (self) {
        self.searchResultsDataSource = self;
        self.searchResultsDelegate = self;
    }
    return self;
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70.f;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}


#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.searchResult.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}

#pragma mark - getter
- (NSMutableArray *)searchResult
{
    if (_searchResult == nil) {
        _searchResult = [[NSMutableArray alloc] init];
    }
    return _searchResult;
}

@end
