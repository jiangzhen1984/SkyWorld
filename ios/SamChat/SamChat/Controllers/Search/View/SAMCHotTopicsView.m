//
//  SAMCHotTopicsView.m
//  SamChat
//
//  Created by HJ on 5/18/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCHotTopicsView.h"
#import "SAMCHotTopicCellModel.h"
#import "SamChatClient.h"
#import "MJRefresh.h"

@interface SAMCHotTopicsView () <UITableViewDataSource,UITableViewDelegate,UIScrollViewDelegate>

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) NSMutableArray *hotTopics;

@property (nonatomic, assign) NSTimeInterval updateTimePre;
@property (nonatomic, assign) NSInteger currentCount;

@end

@implementation SAMCHotTopicsView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews
{
    self.backgroundColor = [UIColor yellowColor];
    
    _titleLabel = [[UILabel alloc] init];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.text = @"热门搜索";
    _titleLabel.textAlignment = NSTextAlignmentCenter;
    _titleLabel.backgroundColor = [UIColor lightGrayColor];
    [self addSubview:_titleLabel];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    _tableView.translatesAutoresizingMaskIntoConstraints = NO;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor greenColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self addSubview:self.tableView];
    
    __weak typeof(self) weakSelf = self;
    // 下拉刷新
    self.tableView.mj_header= [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        [weakSelf asyncLoadHotTopicsFromServerWithReset:YES];
    }];
    
    // 上拉刷新
    self.tableView.mj_footer = [MJRefreshBackNormalFooter footerWithRefreshingBlock:^{
        [weakSelf asyncLoadHotTopicsFromServerWithReset:NO];
    }];
    
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_titleLabel]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_tableView)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[_titleLabel(44)][_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel,_tableView)]];
}

- (void)dealloc
{
    self.tableView.delegate = nil;
    self.tableView.dataSource = nil;
}

//- (void)layoutSubviews{
//    [super layoutSubviews];
//    self.tableView.frame = CGRectMake(0, 44, self.frame.size.width, self.frame.size.height-44);
//    self.titleLabel.frame = CGRectMake(0, 0, self.frame.size.width, 44);
//}

#pragma mark - UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.hotTopics.count;
}

-(UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *HotQuestionCellIdentifier=@"HotQuestionTableCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:HotQuestionCellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:HotQuestionCellIdentifier];
    }
    SAMCHotTopicCellModel *topic = self.hotTopics[indexPath.row];
    cell.textLabel.text = topic.name;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40;
}

#pragma mark - UITableViewDelegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.delegate){
        [self.delegate didSelectHotTopic:((SAMCHotTopicCellModel *)self.hotTopics[indexPath.row]).name];
    }
}

#pragma mark - Lazy loading
- (NSMutableArray *)hotTopics
{
    if(_hotTopics == nil){
        _hotTopics = [NSMutableArray arrayWithArray:[[SamChatClient sharedClient].searchManager hotTopicsWithType:0]];
        [self asyncLoadHotTopicsFromServerWithReset:YES];
    }
    return _hotTopics;
}

#pragma mark - Private
- (void)asyncLoadHotTopicsFromServerWithReset:(BOOL)resetflag
{
    if (resetflag) {
        _updateTimePre = 0;
        _currentCount = 0;
    }
    [[SamChatClient sharedClient].searchManager queryTopicListWithOptType:0
                                                                topicType:0
                                                             currentCount:_currentCount
                                                            updateTimePre:_updateTimePre
                                                               completion:^(NSDictionary *response, NSError *error) {
       DDLogDebug(@"response: %@", response);
       [self.tableView.mj_header endRefreshing];
       [self.tableView.mj_footer endRefreshing];
       if (error == nil) {
           NSArray *topics = [self convertJsonArrayToTopicsArray:response[SKYWORLD_TOPICS]];
           _currentCount += [topics count];
           _updateTimePre = [response[SKYWORLD_QUERY_TIME] doubleValue];
           if (resetflag) {
               [self.hotTopics removeAllObjects];
           }
           [self.hotTopics addObjectsFromArray:topics];
           [[SamChatClient sharedClient].searchManager updateHotTopicsWithArray:topics];
           [self.tableView reloadData];
       }
    }];
}

- (NSArray *)convertJsonArrayToTopicsArray:(NSArray *)topicsJson
{
    NSMutableArray *topics = [[NSMutableArray alloc] init];
    for (id topicDictionary in topicsJson) {
        if([topicDictionary isKindOfClass:[NSDictionary class]]){
            SAMCHotTopicCellModel *topic = [[SAMCHotTopicCellModel alloc] init];
            topic.type = [((NSDictionary *)topicDictionary)[SKYWORLD_TOPIC_TYPE] integerValue];
            topic.name = ((NSDictionary *)topicDictionary)[SKYWORLD_NAME];
            if((topic.name) && (topic.name.length > 0)){
                [topics addObject:topic];
            }
        }
    }
    return topics;
}

@end
