//
//  SAMCCountryCodeController.m
//  SamChat
//
//  Created by HJ on 5/16/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCCountryCodeController.h"

@interface SAMCCountryCodeController() <UITableViewDataSource,UITableViewDelegate,UISearchBarDelegate,UISearchDisplayDelegate>

@property (nonatomic, strong) UITableView *tableViewCountryCode;
@property (nonatomic, strong) UISearchDisplayController *searchResultController;
@property (nonatomic, strong) UISearchBar *searchBar;
@property (nonatomic, strong) NSDictionary *countryCodeDictionary;
@property (nonatomic, strong) NSArray *indexArray;
@property (nonatomic, strong) NSMutableArray *searchResultArray;

@end

@implementation SAMCCountryCodeController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view setBackgroundColor:[UIColor whiteColor]];
    [self.navigationItem setTitle:@"国家代码"];
    [self setupSubviews];
}

#pragma mark - lazy load
- (NSMutableArray *)searchResultArray
{
    if (_searchResultArray == nil) {
        _searchResultArray = [[NSMutableArray alloc] init];
    }
    return _searchResultArray;
}

-(void)setupSubviews{
    self.tableViewCountryCode = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableViewCountryCode.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.tableViewCountryCode.dataSource = self;
    self.tableViewCountryCode.delegate = self;
    self.tableViewCountryCode.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    [self.view addSubview:self.tableViewCountryCode];
    
    self.searchBar = [[UISearchBar alloc] init];
    [self.searchBar sizeToFit];
    [self.searchBar setDelegate:self];
    self.searchBar.autocapitalizationType = UITextAutocapitalizationTypeNone;
    self.tableViewCountryCode.tableHeaderView = self.searchBar;
    
    self.searchResultController = [[UISearchDisplayController alloc] initWithSearchBar:self.searchBar contentsController:self];
    self.searchResultController.delegate = self;
    self.searchResultController.searchResultsDataSource = self;
    self.searchResultController.searchResultsDelegate = self;
    
    NSString *countryCodeplistPath = nil;
    if ([self isCurrentLanguageChinese]) {
        countryCodeplistPath = [[NSBundle mainBundle] pathForResource:@"CountryCodeListCh" ofType:@"plist"];
    }else{
        countryCodeplistPath = [[NSBundle mainBundle] pathForResource:@"CountryCodeListEn" ofType:@"plist"];
    }
    self.countryCodeDictionary = [NSDictionary dictionaryWithContentsOfFile:countryCodeplistPath];
    
    self.indexArray = [self.countryCodeDictionary allKeys];
    self.indexArray = [self.indexArray sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        return [obj1 compare:obj2];
    }];
}

#pragma mark - UITableViewDataSource
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (tableView == self.tableViewCountryCode) {
        return [self.indexArray count];
    }else{
        return 1;
    }
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (tableView == self.tableViewCountryCode) {
        NSArray *array = [self.countryCodeDictionary objectForKey:self.indexArray[section]];
        return [array count];
    }else{
        return [self.searchResultArray count];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (tableView == self.tableViewCountryCode) {
        return 30;
    }else {
        return 0;
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 44;
}

- (NSString *) tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return self.indexArray[section];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString * cellId = @"cellId";
    UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
    }
    
    if (tableView == self.tableViewCountryCode) {
        NSString *key = self.indexArray[indexPath.section];
        cell.textLabel.text = [[self.countryCodeDictionary objectForKey:key] objectAtIndex:indexPath.row];
        return cell;
    }else{
        cell.textLabel.text = [self.searchResultArray objectAtIndex:indexPath.row];
        return cell;
    }
}

-(NSArray<NSString *> *)sectionIndexTitlesForTableView:(UITableView *)tableView
{
    if (tableView == self.tableViewCountryCode) {
        return self.indexArray;
    }else{
        return nil;
    }
}

-(NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index
{
    if (tableView == self.tableViewCountryCode) {
        return index;
    }else{
        return 0;
    }
}

#pragma mark - UITableViewDelegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    
    if (self.selecteCountryCodeBlock != nil) {
        self.selecteCountryCodeBlock(cell.textLabel.text);
        self.selecteCountryCodeBlock = nil;
    }
    [self.navigationController popViewControllerAnimated:YES];
//    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UISearchBarDelegate
-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    [self.searchResultArray removeAllObjects];
    for (NSArray *array in [self.countryCodeDictionary allValues]) {
        NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(NSString *value, NSDictionary<NSString *,id> * _Nullable bindings) {
            NSRange range = [value rangeOfString:searchText options:NSCaseInsensitiveSearch];
            return range.location != NSNotFound;
        }];
        [self.searchResultArray addObjectsFromArray:[array filteredArrayUsingPredicate:predicate]];
    }
    [self.searchResultController.searchResultsTableView reloadData];
}

#pragma mark - Private
- (BOOL)isCurrentLanguageChinese
{
    NSString *currentLaunguage = [[NSLocale preferredLanguages] firstObject];
    DDLogDebug(@"launguage: %@", currentLaunguage);
    // @"zh-Hans" @"zh-Hant" @"zh-HK"
    NSRange range = [currentLaunguage rangeOfString:@"zh-Hans" options:NSCaseInsensitiveSearch];
    return range.location != NSNotFound;
}

@end
